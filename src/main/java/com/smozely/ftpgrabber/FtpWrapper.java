package com.smozely.ftpgrabber;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class FtpWrapper {

    private static final Logger log = LoggerFactory.getLogger(FtpWrapper.class);

    private static final String TEMP_FILE_EXTN = ".writing";

    private final FtpConfig config;

    private FTPClient client;

    @Autowired
    public FtpWrapper(FtpConfig config) {
        this.config = config;
    }

    public void connect() {
        try {
            client = new FTPClient();
            client.connect(config.host);

            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                client.disconnect();
                log.error("Failed to connect to {}", config.host);
                throw new FtpWrapperException("Failed to Connect");
            } else {
                log.info("Connected to {}", config.host);
            }

            boolean loggedIn = client.login(config.username, config.password);
            if (!loggedIn) {
                log.error("Failed to login to {}, with user {}", config.host, config.username);
                throw new FtpWrapperException("Failed to login");
            }

            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory(config.fromDir);
        } catch (IOException e) {
            throw new FtpWrapperException("Exception connecting to FTP Server", e);
        }
    }

    public void disconnect() {
        if (client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
            } catch (IOException ioe) {
                // NOOP
            }
        }
    }

    public List<String> listItemsToSync() {
        try {
            ArrayList<String> result = new ArrayList<>();
            client.changeWorkingDirectory(config.fromDir);
            result.addAll(listItemsInDirectory(""));
            return result;
        } catch (IOException e) {
            throw new FtpWrapperException("Exception listing files on remote server", e);
        }
    }

    public void retrieveFile(String filename, File toFile) {
        try {
            log.info("Getting File [{}]", filename);

            Path outputPath = Paths.get(toFile.getAbsolutePath(), filename);
            if (Files.exists(outputPath)) {
                log.debug("File already exists, skipping");
                return;
            }

            Path serverPath = Paths.get(config.fromDir, filename);
            String remoteDirName = serverPath.getParent().toString();
            log.debug("Changing to remote dir [{}]", remoteDirName);
            client.changeWorkingDirectory(remoteDirName);

            Files.createDirectories(outputPath.getParent());
            File tempFile = Paths.get(toFile.getAbsolutePath(), filename + TEMP_FILE_EXTN).toFile();

            if (tempFile.exists()) {
                long fileLength = tempFile.length();
                log.debug("Temp File exists for [{}] attempting to restart from [{}]", tempFile.getName(), fileLength);
                client.setRestartOffset(fileLength);
            }

            try (BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile, true))) {
                client.retrieveFile(outputPath.getFileName().toString(), out);
                log.info("File received [{}]", filename);
            }
            tempFile.renameTo(outputPath.toFile());
        } catch (IOException e) {
            throw new FtpWrapperException("Exception retrieving file", e);
        }
    }

    private List<String> listItemsInDirectory(String directory) throws IOException {
        ArrayList<String> result = new ArrayList<>();
        String remoteDirName = Paths.get(config.fromDir, directory).toString();

        log.debug("Changing to remote dir [{}]", remoteDirName);
        client.changeWorkingDirectory(remoteDirName);

        FTPFile[] files = client.listFiles();
        for (FTPFile file : files) {
            String nextItemName = Paths.get(directory, file.getName()).toString();
            if (file.isDirectory()) {
                log.debug("Processing Directory [{}]", file.getName());
                result.addAll(listItemsInDirectory(nextItemName));
            } else {
                log.debug("Processing File [{}]", file.getName());
                result.add(nextItemName);
            }
        }
        return result;
    }

}

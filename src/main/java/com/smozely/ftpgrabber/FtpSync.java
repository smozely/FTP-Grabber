package com.smozely.ftpgrabber;

import org.apache.commons.net.ftp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class FtpSync {

    private static final Logger log = LoggerFactory.getLogger(FtpSync.class);

    private final String host;

    private final String username;

    private final String password;

    private final String fromDir;

    private final File toDir;

    @Autowired
    public FtpSync(@Value("${ftp.host}") String host, @Value("${ftp.user}") String username, @Value("${ftp.password}") String password, @Value("${ftp.remote.dir}") String fromDir, @Value("${ftp.local.dir}") String toDir) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.fromDir = fromDir;
        this.toDir = new File(toDir);

        if (!this.toDir.exists()) {
            this.toDir.mkdirs();
        }
    }

    public void run() {
        log.info("Sync Started");
        FTPClient client = new FTPClient();

        try {
            client.connect(host);

            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                client.disconnect();
                log.error("Failed to connect to {}", host);
                return;
            } else {
                log.info("Connected to {}", host);
            }

            boolean loggedIn = client.login(username, password);
            if (!loggedIn) {
                log.error("Failed to login to {}, with user {}", host, username);
                return;
            }

            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory(fromDir);

            log.info("Connected and Logged in Starting file processing");
            processFolder(client, toDir);

            client.logout();
        } catch (IOException e) {
            log.error("Sync Failed", e);
        } finally {
            if(client.isConnected()) {
                try {
                    client.disconnect();
                } catch(IOException ioe) {
                    // NOOP
                }
            }
        }

        log.info("Sync Finished");
    }

    private void processFolder(FTPClient client, File outFolder) throws IOException {
        List<FTPFile> files = Arrays.asList(client.listFiles());

        files.forEach(file -> {
            try {
                log.info("Processing [{}]", file.getName());
                if (file.isDirectory()) {
                    client.changeWorkingDirectory(file.getName());
                    File nextFolder = new File(outFolder, file.getName());
                    if (!nextFolder.exists()) {
                        nextFolder.mkdirs();
                    }
                    processFolder(client, nextFolder);
                    client.changeToParentDirectory();
                } else {
                    copyFileToLocal(client, outFolder, file);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void copyFileToLocal(FTPClient client, File currentDir, FTPFile file) throws IOException {

        File outFile = new File(currentDir, file.getName());
        if (!outFile.exists()) {
            log.info("About to download [{}] with Size [{}]", file.getName(), file.getSize());
            File tempFile = new File(currentDir, file.getName() + ".writing");

            if (tempFile.exists()) {
                long fileLength = tempFile.length();
                log.info("Resuming download of [{}] from [{}]", file.getName(), fileLength);
                client.setRestartOffset(fileLength);
            }

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile, true));
            client.retrieveFile(file.getName(), out);
            out.close();
            log.info("File received [{}]", file.getName());

            if (tempFile.length() != file.getSize()) {
                log.error("Shit has hit the fan, file sizes are different Should Be [{}] was [{}]", file.getSize(), tempFile.length());
                tempFile.delete();
            } else {
                tempFile.renameTo(outFile);
            }
        }

    }


}

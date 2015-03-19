package com.smozely.ftpgrabber;

import com.smozely.ftpgrabber.filters.FileFilter;
import com.smozely.ftpgrabber.notifiers.FileSyncNotifier;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@Component
public class FtpSync {

    private static final Logger log = LoggerFactory.getLogger(FtpSync.class);

    private final FtpConfig config;

    private final File toDir;

    private final List<FileFilter> filters;

    private final List<FileSyncNotifier> notifiers;

    @Autowired
    public FtpSync(FtpConfig config, @Value("${ftp.local.dir}") String toDir,
                   List<FileFilter> filters, List<FileSyncNotifier> notifiers) {
        this.config = config;
        this.toDir = new File(toDir);
        if (!this.toDir.exists()) {
            this.toDir.mkdirs();
        }
        this.filters = filters;
        this.notifiers = notifiers;
    }

    public void run() {
        log.info("Sync Started");
        FTPClient client = new FTPClient();

        try {
            client.connect(config.host);

            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                client.disconnect();
                log.error("Failed to connect to {}", config.host);
                return;
            } else {
                log.info("Connected to {}", config.host);
            }

            boolean loggedIn = client.login(config.username, config.password);
            if (!loggedIn) {
                log.error("Failed to login to {}, with user {}", config.host, config.username);
                return;
            }

            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory(config.fromDir);

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
                    // TODO Filter should get full path, not just name
                    // String fileName = outFile.getPath().substring(toDir.getPath().length() + 1);
                    if (filters.stream().allMatch(filter -> filter.test(file.getName()))) {
                        copyFileToLocal(client, outFolder, file);
                        notifiers.forEach(notifier -> notifier.synced(file.getName()));
                    } else {
                        log.info("Filtered [{}]", file.getName());
                    }
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

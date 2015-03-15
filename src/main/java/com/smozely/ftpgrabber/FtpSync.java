package com.smozely.ftpgrabber;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
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
        log.info("Running Sync : {}", host);
        FTPClient client = new FTPClient();
        try {
            client.connect(host);
            client.login(username, password);

            // TODO Check we are really logged in
            System.out.println(client.getReplyString());
            client.setFileType(FTP.BINARY_FILE_TYPE);

            List<FTPFile> files = Arrays.asList(client.listFiles(fromDir));

            files.forEach(file -> {
                try {
                    log.info("Processing [{}]", file.getName());
                    if (file.isDirectory()) {
                        processFolder(client, file);
                    } else {
                        copyFileToLocal(client, file);
                    }
                } catch (IOException e) {

                    e.printStackTrace();
                }
            });

            client.logout();
        } catch (IOException e) {
            log.error("Sync Failed", e);
        }
    }

    private void processFolder(FTPClient client, FTPFile file) throws IOException {
        List<FTPFile> files = Arrays.asList(client.listFiles(fromDir + file.getName()));

//        files.forEach(file -> {


//        });
    }

    private void copyFileToLocal(FTPClient client, FTPFile file) throws IOException {
        File outFile = new File(toDir, file.getName());
        if (!outFile.exists()) {
            log.info("About to download [{}] with Size [{}]", file.getName(), file.getSize());
            File tempFile = new File(toDir, file.getName() + ".writing");

            if (tempFile.exists()) {
                long fileLength = tempFile.length();
                client.setRestartOffset(fileLength);
            }

            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(tempFile, true));
            client.retrieveFile(fromDir + "/" + file.getName(), out);
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

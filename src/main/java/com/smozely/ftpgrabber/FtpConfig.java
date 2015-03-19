package com.smozely.ftpgrabber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FtpConfig {

    public final String host;

    public final String username;

    public final String password;

    public final String fromDir;

    @Autowired
    public FtpConfig(@Value("${ftp.host}") String host, @Value("${ftp.user}") String username, @Value("${ftp.password}") String password, @Value("${ftp.remote.dir}") String fromDir) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.fromDir = fromDir;
    }
}

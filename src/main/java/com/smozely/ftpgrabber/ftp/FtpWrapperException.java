package com.smozely.ftpgrabber.ftp;

public class FtpWrapperException extends RuntimeException {
    public FtpWrapperException(String message) {
        super(message);
    }

    public FtpWrapperException(String message, Throwable cause) {
        super(message, cause);
    }
}

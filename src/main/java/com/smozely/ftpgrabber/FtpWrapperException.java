package com.smozely.ftpgrabber;

public class FtpWrapperException extends RuntimeException {
    public FtpWrapperException(String message) {
        super(message);
    }

    public FtpWrapperException(String message, Throwable cause) {
        super(message, cause);
    }
}

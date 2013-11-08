package com.stevemosley.ftpgrabber.filters;

import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.file.filters.AbstractFileListFilter;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Filter (using an in-memory history) to ensure that a FTPFile is only downloaded once (by name of file).
 *
 * Used because FTPFile provides no useful equals method, so spring provided AcceptOnceFileListFilter will not work.
 */
public class AcceptOnceFTPFileListFilter extends AbstractFileListFilter<FTPFile> {

    private final Queue<String> seen = new LinkedBlockingDeque<String>();

    private final Object monitor = new Object();

    @Override
    protected boolean accept(FTPFile file) {
        String fileName = file.getName();
        synchronized (this.monitor) {
            if (this.seen.contains(fileName)) {
                return false;
            } else {
                this.seen.add(fileName);
                return true;
            }
        }

    }
}

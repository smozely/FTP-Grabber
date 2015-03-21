package com.smozely.ftpgrabber.filters;

import com.smozely.ftpgrabber.DownloadedFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AlreadyDownloadedFilter implements FileFilter {


    private final DownloadedFileStore store;

    @Autowired
    public AlreadyDownloadedFilter(DownloadedFileStore store) {
        this.store = store;
    }

    @Override
    public boolean test(String fileName) {
        return !store.contains(fileName);
    }
}

package com.smozely.ftpgrabber.notifiers;

import com.smozely.ftpgrabber.FileSyncNotifier;
import com.smozely.ftpgrabber.store.DownloadedFileStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DownloadedFileNotifier implements FileSyncNotifier {

    private final DownloadedFileStore store;

    @Autowired
    public DownloadedFileNotifier(DownloadedFileStore store) {
        this.store = store;
    }

    @Override
    public void synced(String fileName) {
        store.recordFileAsDownloaded(fileName);
    }
}

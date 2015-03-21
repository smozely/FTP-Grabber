package com.smozely.ftpgrabber;

import com.smozely.ftpgrabber.filters.FileFilter;
import com.smozely.ftpgrabber.notifiers.FileSyncNotifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class Synchronizer {

    private static final Logger log = LoggerFactory.getLogger(Synchronizer.class);

    private final FtpWrapper clientWrapper;

    private final File toDir;

    private final List<FileFilter> filters;

    private final List<FileSyncNotifier> notifiers;

    @Autowired
    public Synchronizer(FtpWrapper clientWrapper, @Value("${ftp.local.dir}") String toDir,
                        List<FileFilter> filters, List<FileSyncNotifier> notifiers) {
        this.clientWrapper = clientWrapper;
        this.toDir = new File(toDir);
        if (!this.toDir.exists()) {
            this.toDir.mkdirs();
        }
        this.filters = filters;
        this.notifiers = notifiers;
    }

    public void run() {
        log.info("Sync Started");

        try {
            clientWrapper.connect();

            List<String> itemsToSync = clientWrapper.listItemsToSync();

            itemsToSync.forEach(item -> {
                if (filters.stream().allMatch(filter -> filter.test(item))) {
                    clientWrapper.retrieveFile(item, toDir);
                    notifiers.forEach(notifier -> notifier.synced(item));
                } else {
                    log.info("Filtered [{}]", item);
                }
            });
        } catch (FtpWrapperException e) {
            log.error("Sync Failed", e);
        } finally {
            clientWrapper.disconnect();
        }

        log.info("Sync Finished");
    }

}

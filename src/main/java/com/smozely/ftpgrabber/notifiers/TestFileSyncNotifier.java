package com.smozely.ftpgrabber.notifiers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dumb File Filter that just logs that is has been called.
 * <p>
 * For Testing the Infrastructure.
 */
@Component
public class TestFileSyncNotifier implements FileSyncNotifier {

    private static final Logger log = LoggerFactory.getLogger(TestFileSyncNotifier.class);

    @Override
    public void synced(String fileName) {
        log.info("TestFileSyncNotifier called for [{}]", fileName);
    }
}

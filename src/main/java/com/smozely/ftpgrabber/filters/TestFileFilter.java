package com.smozely.ftpgrabber.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Dumb File Filter that just logs that is has been called.
 * <p>
 * For Testing the Infrastructure.
 */
@Component
public class TestFileFilter implements FileFilter {

    private static final Logger log = LoggerFactory.getLogger(TestFileFilter.class);

    @Override
    public boolean test(String fileName) {
        log.info("TestFilerFilter called for [{}]", fileName);
        return true;
    }
}

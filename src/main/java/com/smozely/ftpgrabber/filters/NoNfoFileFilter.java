package com.smozely.ftpgrabber.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * File Filter that will not download .nfo files
 */
@Component
public class NoNfoFileFilter implements FileFilter {

    private static final Logger log = LoggerFactory.getLogger(NoNfoFileFilter.class);

    @Override
    public boolean test(String fileName) {
        log.info("TestFilerFilter called for [{}]", fileName);
        if (fileName.endsWith(".nfo")) {
            return false;
        } else {
            return true;
        }
    }
}

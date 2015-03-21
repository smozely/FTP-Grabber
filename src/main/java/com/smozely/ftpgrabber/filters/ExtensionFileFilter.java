package com.smozely.ftpgrabber.filters;

import com.smozely.ftpgrabber.FileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * File filter that will reject files that end with anything from the provided list of
 * Strings.
 * <p>
 * Intended to be used with file extensions i.e. .nfo, .jpg
 */
@Component
public class ExtensionFileFilter implements FileFilter {

    private final String[] extensionsToFilter;

    @Autowired
    public ExtensionFileFilter(@Value("${filters.extns}") String[] extensionsToFilter) {
        this.extensionsToFilter = extensionsToFilter;
    }

    @Override
    public boolean test(String fileName) {
        return Stream.of(extensionsToFilter).noneMatch(value -> fileName.endsWith(value));
    }
}

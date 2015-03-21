package com.smozely.ftpgrabber.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * File filter that will reject files that contain any of the values from the provided list of
 * Strings.
 *
 * Intended to be used as a way to filter files with particular names.
 */
@Component
public class NameFileFilter implements FileFilter {

    private final String[] namesToFilter;

    @Autowired
    public NameFileFilter(@Value("${filters.names}") String... namesToFilter) {
        this.namesToFilter = namesToFilter;
    }

    @Override
    public boolean test(String fileName) {
        return Stream.of(namesToFilter).noneMatch(value -> fileName.contains(value));
    }

}

package com.stevemosley.ftpgrabber.enricher;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileToOutputDirectoryEnricher {

    private static final Logger LOG = LoggerFactory.getLogger(FileToOutputDirectoryEnricher.class);
    private final Map<Pattern, String> rules;

    public FileToOutputDirectoryEnricher(Map<String, String> rules) {
        Map<Pattern, String> tempRules = Maps.newHashMap();

        for (Map.Entry<String, String> current : rules.entrySet()) {
            tempRules.put(Pattern.compile(current.getKey()), current.getValue());
        }
        this.rules = ImmutableMap.copyOf(tempRules);
    }

    public String determineOutputFolderForFile(File message) {
        String fileName = message.getName();
        for (Map.Entry<Pattern, String> current : rules.entrySet()) {
            Matcher m = current.getKey().matcher(fileName);
            if (m.matches()) {
                return m.replaceAll(current.getValue());
            }
        }
        throw new RuntimeException("No match found for current file " + fileName + ", Must be a configuration error");
    }
}

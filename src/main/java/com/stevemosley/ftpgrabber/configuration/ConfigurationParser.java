package com.stevemosley.ftpgrabber.configuration;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;

public class ConfigurationParser {

    private final Map<String, String> config;

    public ConfigurationParser(Map<String, String> config) {
        this.config = ImmutableMap.copyOf(config);
    }

    public List<String> determineRuleList() {
        return Lists.newArrayList(config.keySet());
    }

    public Map<String, String> determineRuleMap() {
        return config;
    }

}

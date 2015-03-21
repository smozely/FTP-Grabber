package com.smozely.ftpgrabber;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Component
public class DownloadedFileStore {

    private static final Logger log = LoggerFactory.getLogger(DownloadedFileStore.class);

    private final File storeFile;

    private List<String> files = new ArrayList<>();

    @Autowired
    public DownloadedFileStore(@Value("${store.name}") File storeFile) {
        this.storeFile = storeFile;

        ensureFilesLoaded();
    }

    public boolean contains(String name) {
        return files.contains(name);
    }

    public void recordFileAsDownloaded(String name) {
        files.add(name);
        try {
            Files.write(storeFile.toPath(), files, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("Problem writing store file", e);
        }
    }

    private void ensureFilesLoaded() {
        try {
            if (!storeFile.exists()) {
                storeFile.createNewFile();
            } else {
                files = Files.readAllLines(storeFile.toPath());
            }
        } catch (IOException e) {
            log.error("Problem reading store file", e);
        }
    }

}

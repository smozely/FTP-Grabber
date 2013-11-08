package com.stevemosley.ftpgrabber.enricher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.io.File;
import java.util.Map;

public class FileToOutputDirectoryEnricherTest {

    @Test
    public void testEnriching() throws Exception {

        // GIVEN
        Map<String, String> config = new ImmutableMap.Builder<String, String>()
                .put(".*TEST(\\d)", "Test/$1")
                .put("TEST.*S(\\d\\d)E(\\d\\d).*", "Test/$1")
                .build();
        FileToOutputDirectoryEnricher underTest = new FileToOutputDirectoryEnricher(config);

        // WHEN / THEN
        assertThat(underTest.determineOutputFolderForFile(new File("---TEST1")), is("Test/1"));
        assertThat(underTest.determineOutputFolderForFile(new File("TEST2")), is("Test/2"));
        assertThat(underTest.determineOutputFolderForFile(new File("TEST3")), is("Test/3"));
        assertThat(underTest.determineOutputFolderForFile(new File("TEST S00E00---")), is("Test/00"));
    }

}

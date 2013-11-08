package com.stevemosley.ftpgrabber.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import com.google.common.collect.Lists;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class MultiRegexFTPFileListFilterTest {

    @Test
    public void testMatchingRegexes() throws Exception {

        // GIVEN
        MultiRegexFTPFileListFilter underTest = new MultiRegexFTPFileListFilter(Lists.newArrayList("ONE.*", ".*TWO", "THREE"));

        // WHEN / THEN
        assertThat(underTest.accept(ftpFileWithName("MONKEY")), is(false));
        assertThat(underTest.accept(ftpFileWithName("ONE---")), is(true));
        assertThat(underTest.accept(ftpFileWithName("---TWO")), is(true));
        assertThat(underTest.accept(ftpFileWithName("THREE")), is(true));
        assertThat(underTest.accept(ftpFileWithName("-THREE-")), is(false));

    }

    private FTPFile ftpFileWithName(String name) {
        FTPFile result = new FTPFile();
        result.setName(name);
        return result;
    }
}

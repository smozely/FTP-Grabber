package com.stevemosley.ftpgrabber.filters;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

public class AcceptOnceFTPFileListFilterTest {

    private AcceptOnceFTPFileListFilter underTest = new AcceptOnceFTPFileListFilter();

    @Test
    public void shouldOnlyAcceptEachFileOnce() {
        // GIVEN
        FTPFile file1 = new FTPFile();
        file1.setName("NAME");

        FTPFile file2 = new FTPFile();
        file2.setName("NAME");

        FTPFile file3 = new FTPFile();
        file3.setName("DIFF_NAME");

        // WHEN / THEN
        assertThat(underTest.accept(file1), is(true));
        assertThat(underTest.accept(file2), is(false));
        assertThat(underTest.accept(file3), is(true));
    }

}

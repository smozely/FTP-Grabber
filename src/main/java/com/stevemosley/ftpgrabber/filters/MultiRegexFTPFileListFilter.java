package com.stevemosley.ftpgrabber.filters;

import com.google.common.collect.ImmutableList;
import org.apache.commons.net.ftp.FTPFile;
import org.springframework.integration.file.filters.AbstractFileListFilter;

import java.util.List;

/**
 * Filter by checking if file name matches a list of Regexes.
 */
public class MultiRegexFTPFileListFilter extends AbstractFileListFilter<FTPFile> {

    private final List<String> regexs;

    public MultiRegexFTPFileListFilter(List<String> regexs) {
        this.regexs = ImmutableList.copyOf(regexs);
    }

    @Override
    protected boolean accept(FTPFile file) {
        String fileName = file.getName();
        for(String current : regexs) {
            if(fileName.matches(current)) {
                return true;
            }
        }
        return false;
    }
}

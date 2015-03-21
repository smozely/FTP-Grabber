package com.smozely.ftpgrabber;

/**
 * Interface to define Filters, used to determine if files should be synced or not.
 */
public interface FileFilter {

    /**
     * Test if this file should be retrieved or not.
     *
     * @param fileName the candidate file to download.
     *
     * @return true if the file is to be sync'ed otherwise false to ignore the file.
     */
    public boolean test(String fileName);

}

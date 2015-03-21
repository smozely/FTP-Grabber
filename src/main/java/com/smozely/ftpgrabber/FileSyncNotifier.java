package com.smozely.ftpgrabber;

/**
 * Interface to define Notifiers, used to tell other components that files have been synced.
 */
public interface FileSyncNotifier {

    public void synced(String fileName);

}

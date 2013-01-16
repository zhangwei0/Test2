package com.wl.magz.downloads;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class DownloadHandler {
    
    private static final int MAX_DOWNLOADS_COUNT = 5;

    private static final String TAG = "DownloadManager";
    private final LinkedHashMap<Long, DownloadInfo> mDownloadsQueue =
            new LinkedHashMap<Long, DownloadInfo>();
    private final HashMap<Long, DownloadInfo> mDownloadsInProgress =
            new HashMap<Long, DownloadInfo>();
    
    private static final DownloadHandler mInstance = new DownloadHandler();
    
    static DownloadHandler getInstance() {
        return mInstance;
    }
    
    synchronized void enqueueDownload(DownloadInfo info) {
        if (!mDownloadsQueue.containsKey(info.mId)) {
            mDownloadsQueue.put(info.mId, info);
            startDownloadThread();
        }
    }
    
    synchronized void dequeueDownload(long mId) {
        //TODO
        //mDownloads
    }
    
    private synchronized void startDownloadThread() {
        Iterator<Long> keys = mDownloadsQueue.keySet().iterator();
        ArrayList<Long> ids = new ArrayList<Long>();
        while (mDownloadsInProgress.size() <=MAX_DOWNLOADS_COUNT
                && keys.hasNext()) {
            Long id = keys.next();
            DownloadInfo info = mDownloadsQueue.get(id);
            info.startDownloadThread();
            mDownloadsInProgress.put(id, info);
            ids.add(id);
        }
        for (Long id: ids) {
            mDownloadsQueue.remove(id);
        }
        
    }
    
    synchronized boolean hasDownloadInQueue(long id) {
        return mDownloadsQueue.containsKey(id) || mDownloadsInProgress.containsKey(id);
    }

}

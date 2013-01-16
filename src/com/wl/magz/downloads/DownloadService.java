package com.wl.magz.downloads;


import java.util.HashMap;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DownloadService extends Service {

    private DownloadHandler mDownloadManager;
    private HashMap<Long, DownloadInfo> mDownloads = new HashMap<Long, DownloadInfo>();

    private UpdateThread mUpdateThread;
    private boolean mPendingUpdate;
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }
    
    public void onCreate() {
        super.onCreate();
        mDownloadManager = DownloadHandler.getInstance();
    }
    
    private void addNewDownload(DownloadInfo info) {
        
    }
    
    private void downloadMagzs() {
        
    }
    
    private void updateFromProvider() {
        synchronized(this) {
            mPendingUpdate = true;
            if (mUpdateThread == null) {
                mUpdateThread = new UpdateThread();
                mUpdateThread.run();
            }
        }
    }
    
    private class UpdateThread extends Thread {
        
        public void run() {
            Process.setThreadPriority(10/* Process.THREAD_PRIORITY_BACKGROUND */);
        
            synchronized(DownloadService.this) {
                
            }
        }
    }

}

package com.wl.magz.downloads;


import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.wl.magz.utils.DBHelper;
import com.wl.magz.utils.Constant.Downloads;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
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
    
    /*
     * Public Api
     */
    public void addNewDownload(String uri) {
        DBHelper.insertDownload(uri);
        updateFromProvider();
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
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            synchronized(DownloadService.this) {
                long now = System.currentTimeMillis();
                Set<Long> idsNoLongerInDatabase = new HashSet<Long>(mDownloads.keySet());
                Cursor cursor = DBHelper.getAllDownloads();
                DownloadInfo.Reader reader = new DownloadInfo.Reader(getContentResolver(), cursor);
                int idColumn = cursor.getColumnIndexOrThrow(Downloads.COLUMN_ID);
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    long id = cursor.getLong(idColumn);
                    idsNoLongerInDatabase.remove(id);
                    DownloadInfo info = mDownloads.get(id);
                    if (info != null) {
                        updateDownload(reader, info, now);
                    } else {
                        info = insertDownload(reader, now);
                    }
                }
                
                for (Long id: idsNoLongerInDatabase) {
                    deleteDownload(id);
                }
            }
        }
    }
    
    private DownloadInfo insertDownload(DownloadInfo.Reader reader, long now) {
        DownloadInfo info = reader.newDownloadInfo(this);
        mDownloads.put(info.mId, info);
        info.startIfReady(now);
        return info;
    }
    
    private void deleteDownload(long id) {
        DownloadInfo info = mDownloads.get(id);
        if (info.mStatus == Downloads.STATUS_RUNNING) {
            info.mStatus = Downloads.STATUS_CANCELED;
        }
        if (info.mFileName != null) {
            new File(info.mFileName).delete();
        }
        mDownloads.remove(info.mId);
    }
    
    private void updateDownload(DownloadInfo.Reader reader, DownloadInfo info, long now) {
        reader.updateFromDatabase(info);
        info.startIfReady(now);
    }

}

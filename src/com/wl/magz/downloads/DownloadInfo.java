package com.wl.magz.downloads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Pair;

public class DownloadInfo {
    
    private Context mContext;

    public long mId;
    public String mUri;
    public String mFileName;
    public String mMimeType;
    public int mStatus;
    public int mControl;
    public int mNumFailed;
    public int mRetryAfter;
    public String mETag;
    public boolean mDeleted;
    public int mAllowedNetworkTypes;
    
    public long mTotalBytes;
    public long mCurrentBytes;
    
    private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
    
    public void startDownloadThread() {
        DownloadThread downloader = new DownloadThread(mContext, this);
        downloader.start();
    }
    
    public Collection<Pair<String, String>> getHeaders() {
        return Collection.unmodifiableList(mRequestHeaders);;
    }
    
    public int checkCanUseNetwork() {
        final NetworkInfo info = getActivityNetworkInfo(mUid);
        if (info == null) {
            return NETWORK_NO_CONNECTION;
        }
        if (DetailedState.BLOCKED.equals(info.getDeatiledState())) {
            return NETWORK_BLOCKED;
        }
        return checkIsNetworkTypeAllowed(info.getType());
    }
    
    private int checkSizeAllowedForNetwork(int networkType) {
        if (mTotalBytes <= 0) {
            return NETWORK_OK;
        }
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_OK;
        }
        long maxBytesOverMobile = getMaxBytesOverMobile();
        if (mTotalBytes > maxBytesOverMobile) {
            return NETWORK_UNUSABLE_DUE_TO_SIZE;
        }
        return NETWORK_OK;
        
    }
    
    private boolean isReadyToStart(long now) {
        if (DownloadManager.getInstance().hasDownloadInQueue(mId)) {
            return false;
        }
        if (mControl == DownloadStatus_CONTROL_PAUSED) {
            return false;
        }
        
        switch (mStatus) {
        case 0:
        case DownloadStatus.STATUS_PENDING;
        case DownloadStatus.STATUS_RUNNING;
            return true;
        case DownloadStatus.STATUS_WAITING_FOR_NETWORK:
        case DownloadStatus.STATUS_QUEUED_FOR_WIFI:
            return checkCanUseNetwork() == NETWORK_OK;
        case DownloadStatus.STATUS_WAITING_TO_RETRY:
            return restartTime(now) <= now;
        case DownloadStatus.STATUS_DEVICE_NOT_FOUND_ERROR:
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
        return false;
    }
    
    void startIfReady(long now, StorageManager storageManager {
        if (!isReadyToStart(now)) {
            return;
        }
        if (mStatus != DownloadStatus.STATUS_RUNNING) {
            mStatus = DownloadStatus.STATUS_RUNNING;
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, mStatus);
            mContext.getContentResolver().update(getAllDownloadUri(), values, null, null);
        }
        
        DownloadManager.getInstance().enqueueDownload(this);
    }
}

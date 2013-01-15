package com.wl.magz.downloads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Pair;

public class DownloadInfo {
    
    public static final int NETWORK_OK = 1;

    /**
     * There is no network connectivity.
     */
    public static final int NETWORK_NO_CONNECTION = 2;

    /**
     * The download exceeds the maximum size for this network.
     */
    public static final int NETWORK_UNUSABLE_DUE_TO_SIZE = 3;

    /**
     * The download exceeds the recommended maximum size for this network, the user must confirm for
     * this download to proceed without WiFi.
     */
    public static final int NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE = 4;

    /**
     * The current connection is roaming, and the download can't proceed over a roaming connection.
     */
    public static final int NETWORK_CANNOT_USE_ROAMING = 5;

    /**
     * The app requesting the download specific that it can't use the current network connection.
     */
    public static final int NETWORK_TYPE_DISALLOWED_BY_REQUESTOR = 6;

    /**
     * Current network is blocked for requesting application.
     */
    public static final int NETWORK_BLOCKED = 7;
    
    private Context mContext;

    public int mUid;
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
        return checkSizeAllowedForNetwork(info.getType());
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
        if (mControl == Downloads.CONTROL_PAUSED) {
            return false;
        }
        
        switch (mStatus) {
        case 0:
        case Downloads.STATUS_PENDING:
        case Downloads.STATUS_RUNNING:
            return true;
        case Downloads.STATUS_WAITING_FOR_NETWORK:
        case Downloads.STATUS_QUEUED_FOR_WIFI:
            return checkCanUseNetwork() == NETWORK_OK;
        case Downloads.STATUS_WAITING_TO_RETRY:
            return restartTime(now) <= now;
        case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
        return false;
    }
    
    void startIfReady(long now, StorageManager storageManager {
        if (!isReadyToStart(now)) {
            return;
        }
        if (mStatus != Downloads.STATUS_RUNNING) {
            mStatus = Downloads.STATUS_RUNNING;
            ContentValues values = new ContentValues();
            values.put(COLUMN_STATUS, mStatus);
            mContext.getContentResolver().update(getAllDownloadUri(), values, null, null);
        }
        
        DownloadManager.getInstance().enqueueDownload(this);
    }
    
    public NetworkInfo getActiveNetworkInfo(int uid) {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }

        final NetworkInfo activeInfo = connectivity.getActiveNetworkInfoForUid(uid);
        if (activeInfo == null) {

        }
        return activeInfo;
    }
    
    public static Long getMaxBytesOverMobile(Context context) {
        try {
            return Settings.Secure.getLong(context.getContentResolver(),
                    Settings.Secure.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
        } catch (SettingNotFoundException exc) {
            return null;
        }
    }
}

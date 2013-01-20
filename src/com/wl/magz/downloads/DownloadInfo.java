package com.wl.magz.downloads;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.wl.magz.utils.Constant.Downloads;
import com.wl.magz.utils.DBHelper;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.Uri;
import android.os.Environment;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.TextUtils;
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
    public String mUserAgent;
    
    public long mTotalBytes;
    public long mCurrentBytes;
    
    public boolean mNoIntegrity;
    
    private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
    
    public static class Reader {
        private ContentResolver mResolver;
        private Cursor mCursor;
        public Reader (ContentResolver resolver, Cursor cursor) {
            mResolver = resolver;
            mCursor = cursor;
        }
        
        public DownloadInfo newDownloadInfo(Context context) {
            DownloadInfo info = new DownloadInfo(context);
            updateFromDatabase(info);
            readRequestHeaders(info);
            return info;
        }
        
        public void updateFromDatabase(DownloadInfo info) {
            info.mId = getLong(Downloads.COLUMN_ID);
            info.mUri = getString(Downloads.COLUMN_URI);
//            info.mNoIntegrity = getInt(Downloads.Impl.COLUMN_NO_INTEGRITY) == 1;
            info.mFileName = getString(Downloads.COLUMN_FILE_NAME);
//            info.mMimeType = getString(Downloads.Impl.COLUMN_MIME_TYPE);
            info.mStatus = getInt(Downloads.COLUMN_STATUS);
//            info.mNumFailed = getInt(Constants.FAILED_CONNECTIONS);
//            int retryRedirect = getInt(Constants.RETRY_AFTER_X_REDIRECT_COUNT);
//            info.mRetryAfter = retryRedirect & 0xfffffff;
//            info.mUserAgent = getString(Downloads.COLUMN_USER_AGENT);
            info.mTotalBytes = getLong(Downloads.COLUMN_TOTAL_BYTES);
            info.mCurrentBytes = getLong(Downloads.COLUMN_CURRENT_BYTES);
            info.mETag = getString(Downloads.COLUMN_ETAG);
//            info.mUid = getInt(Constants.UID);
//            info.mDeleted = getInt(Downloads.Impl.COLUMN_DELETED) == 1;
//            info.mAllowedNetworkTypes = getInt(Downloads.Impl.COLUMN_ALLOWED_NETWORK_TYPES);
            synchronized (this) {
                info.mControl = getInt(Downloads.COLUMN_CONTROL);
            }
        }
        
        private void readRequestHeaders(DownloadInfo info) {
            info.mRequestHeaders.clear();
            Cursor cursor = DBHelper.getDownloadHeaders(info.mUri);
            try {
                int headerIndex =
                        cursor.getColumnIndexOrThrow(Downloads.COLUMN_HEADER);
                int valueIndex =
                        cursor.getColumnIndexOrThrow(Downloads.COLUMN_VALUE);
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    addHeader(info, cursor.getString(headerIndex), cursor.getString(valueIndex));
                }
            } finally {
                cursor.close();
            }
        }

        @SuppressLint("NewApi")
        private void addHeader(DownloadInfo info, String header, String value) {
            info.mRequestHeaders.add(Pair.create(header, value));
        }
        
        private String getString(String column) {
            int index = mCursor.getColumnIndexOrThrow(column);
            String s = mCursor.getString(index);
            return (TextUtils.isEmpty(s)) ? null : s;
        }

        private Integer getInt(String column) {
            return mCursor.getInt(mCursor.getColumnIndexOrThrow(column));
        }

        private Long getLong(String column) {
            return mCursor.getLong(mCursor.getColumnIndexOrThrow(column));
        }
    }
    
    public DownloadInfo (Context context, String uri) {
        mContext = context;
        mUri = uri;
    }
    public DownloadInfo(Context context) {
        mContext = context;
    }
    public void startDownloadThread() {
        DownloadThread downloader = new DownloadThread(mContext, this);
        downloader.start();
    }
    
//    public Collection<Pair<String, String>> getHeaders() {
//        return Collection.unmodifiableList(mRequestHeaders);;
//    }
    
    public List<Pair<String, String>> getHeaders() {
        return mRequestHeaders;
    }
    
    public int checkCanUseNetwork() {
        final NetworkInfo info = getActiveNetworkInfo(mUid);
        if (info == null) {
            return NETWORK_NO_CONNECTION;
        }
//        if (DetailedState.BLOCKED.equals(info.getDetailedState())) {
//            return NETWORK_BLOCKED;
//        }
        return checkSizeAllowedForNetwork(info.getType());
    }
    
    private int checkSizeAllowedForNetwork(int networkType) {
        if (mTotalBytes <= 0) {
            return NETWORK_OK;
        }
        if (networkType == ConnectivityManager.TYPE_WIFI) {
            return NETWORK_OK;
        }
//        long maxBytesOverMobile = getMaxBytesOverMobile();
//        if (mTotalBytes > maxBytesOverMobile) {
//            return NETWORK_UNUSABLE_DUE_TO_SIZE;
//        }
        return NETWORK_OK;
        
    }
    
    private boolean isReadyToStart(long now) {
        if (DownloadHandler.getInstance().hasDownloadInQueue(mId)) {
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
//        case Downloads.STATUS_WAITING_TO_RETRY:
//            return restartTime(now) <= now;
        case Downloads.STATUS_DEVICE_NOT_FOUND_ERROR:
            return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        }
        return false;
    }
    
    void startIfReady(long now) {
        if (!isReadyToStart(now)) {
            return;
        }
        if (mStatus != Downloads.STATUS_RUNNING) {
            mStatus = Downloads.STATUS_RUNNING;
            ContentValues values = new ContentValues();
            values.put(Downloads.COLUMN_STATUS, mStatus);
            DBHelper.updateDownloadWithId(mId, values);
        }
        
        DownloadHandler.getInstance().enqueueDownload(this);
    }
    
    public NetworkInfo getActiveNetworkInfo(int uid) {
        ConnectivityManager connectivity =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return null;
        }

//        final NetworkInfo activeInfo = connectivity.getActiveNetworkInfoForUid(uid);
        final NetworkInfo activeInfo = connectivity.getActiveNetworkInfo();
        if (activeInfo == null) {

        }
        return activeInfo;
    }
/*    
    public static Long getMaxBytesOverMobile(Context context) {
        try {
            return Settings.Secure.getLong(context.getContentResolver(),
                    Settings.Secure.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
        } catch (SettingNotFoundException exc) {
            return null;
        }
    }
    */
}

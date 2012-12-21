package com.wl.magz.downloads;

import android.content.Context;

public class DownloadInfo {
    
    private Context mContext;

    public long mId;
    public String mUri;
    public String mFileName;
    public String mMimeType;
    public int mStatus;
    
    public long mTotalBytes;
    public long mCurrentBytes;
    
    
    public void startDownloadThread() {
        DownloadThread downloader = new DownloadThread(mContext, this);
        downloader.start();
    }
    
}

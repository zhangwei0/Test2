package com.wl.magz.downloads;

import android.content.Context;

public class DownloadThread extends Thread {
    private Context mContext;
    private DownloadInfo mInfo;
    
    public DownloadThread(Context context, DownloadInfo info) {
        mContext = context;
        mInfo = info;
    }
    
    public void run() {
        
    }

}

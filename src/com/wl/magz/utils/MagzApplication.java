package com.wl.magz.utils;

import com.wl.magz.downloads.DownloadService;

import android.app.Application;

public class MagzApplication extends Application{

    @Override
    public void onCreate() {
        DBHelper.init(this);
        DownloadService.start(this);
    }
}

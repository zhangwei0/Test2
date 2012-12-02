package com.wl.magz.utils;

import android.app.Application;

public class MagzApplication extends Application{

    @Override
    public void onCreate() {
        DBHelper.init(this);
    }
}

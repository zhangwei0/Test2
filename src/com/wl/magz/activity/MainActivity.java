package com.wl.magz.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.wl.magz.R;
import com.wl.magz.downloads.DownloadService;
import com.wl.magz.utils.MagzApplication;

import android.os.Bundle;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity {
    DownloadService mDownloadService;

    private static final int SLEEP_SECONDS = 2000;
    FragmentManager mFm;
    Fragment mWelcome;
    Fragment mAllInOne;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mFm = getSupportFragmentManager();
        mWelcome = mFm.findFragmentByTag(WelcomeFragment.TAG);
        mAllInOne = mFm.findFragmentByTag(AllInOneFragment.TAG);
        if (mWelcome == null) {
            mWelcome = new WelcomeFragment();
        }
        if (mAllInOne == null) {
            mAllInOne = new AllInOneFragment();
        }

        FragmentTransaction ft = mFm.beginTransaction();
        ft.add(android.R.id.content, mWelcome, WelcomeFragment.TAG);
        ft.commit();

        startDownloadService();
        toAllInOneFragment();
//        ServiceConnection connection = new ServiceConnection() {
//
//            public void onServiceConnected(ComponentName arg0, IBinder binder) {
//                mDownloadService = ((DownloadService.SBinder)binder).getService();
//                
//            }
//
//            public void onServiceDisconnected(ComponentName name) {
//                mDownloadService = null;
//                
//            }
//            
//        };
//        DownloadService.start(this, connection);
  //      mDownloadService.addNewDownload("http://music.baidu.com/data/music/file?link=http://zhangmenshiting.baidu.com/data2/music/33971202/23473715212400128.mp3?xcode=32454b7bbbfbd01d553396c56deab3f1");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    private void startDownloadService() {
        Intent service = new Intent(this, DownloadService.class);
        startService(service);
    }

    private void toAllInOneFragment() {
        TimerTask task = new TimerTask() {
            public void run() {
                FragmentTransaction ft = mFm.beginTransaction();
                ft.remove(mWelcome);
                ft.add(android.R.id.content, mAllInOne, AllInOneFragment.TAG);
                ft.commit();
            }
        };
        Timer t = new Timer();
        t.schedule(task, SLEEP_SECONDS);
    }
}

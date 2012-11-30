package com.wl.magz.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.wl.magz.R;
import com.wl.magz.service.DownloadService;

import android.os.Bundle;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;

public class MainActivity extends FragmentActivity {

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

package com.wl.magz.activity;

import com.wl.magz.R;
import com.wl.magz.service.DownloadService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class MainActivity extends Activity {

    private static final int SLEEP_SECONDS = 0;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        startDownloadService();
        toBookShelfActivity();
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

    private void toBookShelfActivity() {
//        Handler h = new Handler() {
//            public void handleMessage(Message msg) {
//                Intent i = new Intent(MainActivity.this, BookShelfActivity.class);
//                startActivity(i);
//                finish();
//            }
//        };
//
//        h.sendEmptyMessageDelayed(0, 1500 /* Delayed */);

        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(SLEEP_SECONDS);
                } catch (InterruptedException e) {
                    startActivity();
                }
                startActivity();
            }

            private void startActivity() {
                Intent i = new Intent(MainActivity.this, BookshelfActivity.class);
                MainActivity.this.startActivity(i);
                MainActivity.this.finish();
            }
        };

        t.start();
    }
}

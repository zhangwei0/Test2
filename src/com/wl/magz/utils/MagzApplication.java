package com.wl.magz.utils;

import com.wl.magz.downloads.DownloadService;

import android.app.Application;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

public class MagzApplication extends Application{
    private static final String TAG = "MagzApplication";
    public DownloadService mDownloadService;

    @Override
    public void onCreate() {
        DBHelper.init(this);
        ServiceConnection connection = new ServiceConnection() {

            public void onServiceConnected(ComponentName arg0, IBinder binder) {
                mDownloadService = ((DownloadService.SBinder)binder).getService();
                Log.e(TAG, "onServiceConnected:" + (mDownloadService != null));
//                mDownloadService.addNewDownload("http://cwebmail.mail.126.com/js5/read/readpic.jsp?sid=vAuiRVPEwWAxalptGhEEjAxIkTutUcwl&mid=77:1tbiTQ6wzUkZk3P03AAAsm&part=3&mode=inline&l=read&action=open_attach");
//                mDownloadService.addNewDownload("http://www.dyedu.cn/UserFiles/Blog/DJxingyu/UploadFiles/20121227064624602.mp3");
                mDownloadService.addNewDownload("http://bbmedia.qq.com/media/music/audio/200710/13lovesong.mp3");
                mDownloadService.addNewDownload("http://bbmedia.qq.com/media/music/audio/200710/13lovesong.mp3");
                mDownloadService.addNewDownload("http://bbmedia.qq.com/media/music/audio/200710/13lovesong.mp3");
            }

            public void onServiceDisconnected(ComponentName name) {
                mDownloadService = null;
                
            }
            
        };
        DownloadService.start(this, connection);
        
//        mDownloadService.addNewDownload("http://music.baidu.com/data/music/file?link=http://zhangmenshiting.baidu.com/data2/music/33971202/23473715212400128.mp3?xcode=32454b7bbbfbd01d553396c56deab3f1");
    }
}

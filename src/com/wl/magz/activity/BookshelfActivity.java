package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.data.BookshelfItem;
import com.wl.magz.view.AllMgzsAdapter;
import com.wl.magz.view.BookshelfItemView;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.GridView;

public class BookshelfActivity extends Activity implements BookshelfItemView.Callback{

    private static final int COLUMN_INDEX_RECENTLY_READS_PATH = 0; // TODO
    private static final int COLUMN_INDEX_ALL_MGZS_PATH = 1; // TODO

    private int mItemWidth;
    private int mItemHeight;
    
    private ArrayList<BookshelfItem> mRecentlyReads;
    private ArrayList<BookshelfItem> mAllMgzs;
    
    private GridView mRecentGridView;
    private GridView mAllGridView;
    
    private HandlerThread mLoaderThread;
    private LoaderHandler mLoaderHandler;
    private MainHandler mMainHandler;
    
    private static int sLoaderCount = 0;
    private static int sLoaderIndex = 0;
    
    private ArrayList<HandlerThread> mThreads = new ArrayList<HandlerThread>();
    private ArrayList<LoaderHandler> mLoaders = new ArrayList<LoaderHandler>();
    
    private class LoaderHandler extends Handler {
        public LoaderHandler(Looper looper) {
            super(looper);
        }
        public void handleMessage(Message msg) {
            Log.e("LoaderHandler", "handleMessage");
            Bundle b = msg.getData();
            String path = b.getString("path");
            Bitmap bm = getImageFromPath(path);
            b.putParcelable("bitmap", bm);
            Message m = mMainHandler.obtainMessage();
            m.obj = msg.obj;
            m.setData(b);
            m.sendToTarget();
        }
        
        private Bitmap getImageFromPath(String path){
            Log.e("getImageFromPath", "hello");
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(path,option);
            if (bm == null) return null;
            Bitmap newBm = zoom(bm);
            return newBm;
        }
        
        private Bitmap zoom(Bitmap bm) {
            Log.e("zoom", "hello2");
            int width = bm.getWidth();
            int height = bm.getHeight();
            int newWidth = mItemWidth;
            int newHeight = mItemHeight;
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
            return newBm;
        }
    }
    
    private class MainHandler extends Handler {
        public void handleMessage(Message msg) {
            Log.e("MainHandler", "handleMessage");
            BookshelfItem item = (BookshelfItem) msg.obj;
            Bitmap bm = msg.getData().getParcelable("bitmap");
            item.mBitmap = bm;
            BookshelfItemView itemView2 = item.mView;
            if (item.mId == item.mView.mId) {
                itemView2.setImageBitmap(bm);
            }
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bookshelf);
        
        getItemShape();
        BookshelfItemView.setCallback(this);
//        mLoaderThread = new HandlerThread("Load Image");
//        mLoaderThread.start();
//        mLoaderHandler = new LoaderHandler(mLoaderThread.getLooper());
        mMainHandler = new MainHandler();
        //These two may be run in background
        mRecentlyReads = getRecentlyReads();
        mAllMgzs = getAllMgzs();

        initLoadThread();
        
        initViews();
    }
    
    private void initLoadThread() {
        int count = mRecentlyReads.size() + mAllMgzs.size();
        int threadCount = count / 5;
        sLoaderCount = threadCount;
        for (int i = 0; i < threadCount; i ++) {
            HandlerThread thread = new HandlerThread("LoaderThread_" + i);
            thread.start();
            mThreads.add(thread);
            LoaderHandler loader = new LoaderHandler(thread.getLooper());
            mLoaders.add(loader);
        }
    }

    private void getItemShape() {
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        mItemWidth = display.widthPixels / 3;
        mItemHeight = (int) (display.heightPixels / 5);
        BookshelfItemView.mImageWidth = mItemWidth;
        BookshelfItemView.mImageHeight = mItemHeight;
        
    }
    
    private void initViews() {
        initRecentReadsView();
        initAllMgzsView();
    }
    
    private void initRecentReadsView() {
        mRecentGridView = (GridView) findViewById(R.id.recently_reads);
        if (mRecentlyReads == null) return;
        AllMgzsAdapter adapter = new AllMgzsAdapter(this, mRecentlyReads);
        mRecentGridView.setAdapter(adapter);
        mRecentGridView.setNumColumns(3);
    }
    
    private void initAllMgzsView() {
        mAllGridView = (GridView) findViewById(R.id.all_mgzs);
        if (mAllMgzs == null) return;

        AllMgzsAdapter adapter = new AllMgzsAdapter(this, mAllMgzs);
        mAllGridView.setAdapter(adapter);
       // mAllGridView.setNumColumns(3);
    }
    
    private ArrayList<BookshelfItem> getRecentlyReads() {
        ArrayList<BookshelfItem> list = new ArrayList<BookshelfItem>();
        Cursor paths = getRecentlyReadsPath();
        if (paths != null) {
            int count = paths.getCount();
            for (int i = 0; i < count; i ++) {
                String path = paths.getString(COLUMN_INDEX_RECENTLY_READS_PATH);
                BookshelfItem item = new BookshelfItem(BookshelfItem.TYPE_RECENTLY_READS, path, i);
                list.add(item);
            }
        }
        
        //TEST
        for (int i = 90; i < 93; i ++) {
            String path = Environment.getExternalStorageDirectory().toString() + "/test2.jpg";
            BookshelfItem item = new BookshelfItem(BookshelfItem.TYPE_RECENTLY_READS, path, i);
            list.add(item);
        }
        
        return list;
    }
    
    private ArrayList<BookshelfItem> getAllMgzs() {
        ArrayList<BookshelfItem> list = new ArrayList<BookshelfItem>();
        Cursor paths = getAllMgzsPath();
        if (paths != null) {
            int count = paths.getCount();
            for (int i = 0; i < count; i ++) {
                String path = paths.getString(COLUMN_INDEX_ALL_MGZS_PATH);
                BookshelfItem item = new BookshelfItem(BookshelfItem.TYPE_ALL_MGZS, path, i);
                //TODO
                //init all data of this item
                list.add(item);
            }
        }
        
      //TEST
        for (int i = 0; i < 50; i ++) {
            String path = Environment.getExternalStorageDirectory().toString() + "/test" + i + ".jpg";
            BookshelfItem item = new BookshelfItem(BookshelfItem.TYPE_RECENTLY_READS, path, i);
            list.add(item);
        }
        return list;
    }
    
    private Cursor getRecentlyReadsPath() {
        //get the recently reads images's path from DB
        //TODO
        return null;
    }
    
    private Cursor getAllMgzsPath() {
        //get all mgzs images's path from DB
        //TODO
        return null;
    }
    
    public void onDestroy() {
        super.onDestroy();
//        
//        int count = mLoaders.size();
//        for(int i = 0; i < count; i ++)
//        
//        mLoaderHandler = null;
//        Looper looper = mLoaderThread.getLooper();
//        if (looper != null) {
//            looper.quit();
//        }
//        mLoaderThread = null;
    }

    public void loadAndSetImage(String path, BookshelfItemView itemView,
            BookshelfItem item) {
        LoaderHandler loader = mLoaders.get(sLoaderIndex % sLoaderCount);
        
        Message m = loader.obtainMessage();
        m.obj = item;
        Bundle b = new Bundle();
        b.putString("path", path);
        m.setData(b);
        loader.sendMessage(m);
        
        sLoaderIndex ++;
    }
}

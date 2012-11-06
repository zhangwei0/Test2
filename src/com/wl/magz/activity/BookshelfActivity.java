package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.view.BookshelfItem;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

public class BookshelfActivity extends Activity {

    private static final int COLUMN_INDEX_RECENTLY_READS_PATH = 0; // TODO
    private static final int COLUMN_INDEX_ALL_MGZS_PATH = 1; // TODO

    private int mItemWidth;
    private int mItemHeight;
    
    private ArrayList<BookshelfItem> mRecentlyReads;
    private ArrayList<BookshelfItem> mAllMgzs;
    
    private LinearLayout mLinearLayout;
    private GridView mGridView;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bookshelf);
        
        getItemShape();
        
        //These two may be run in background
        mRecentlyReads = getRecentlyReads();
        mAllMgzs = getAllMgzs();
        
        getItemShape();
        initViews();
    }
    
    private void getItemShape() {
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        mItemWidth = display.widthPixels / 3;
        mItemHeight = display.heightPixels / 4;
    }
    
    private void initViews() {
        initRecentReadsView();
        initAllMgzsView();
    }
    
    private void initRecentReadsView() {
        mLinearLayout = (LinearLayout) findViewById(R.id.recently_reads);
        if (mRecentlyReads == null) return;
        int count = mRecentlyReads.size();
        for (int i = 0; i < count; i ++) {
            BookshelfItem item = mRecentlyReads.get(i);
            mLinearLayout.addView(item.getView(), mItemWidth,mItemHeight);
        }
    }
    
    private void initAllMgzsView() {
        mGridView = (GridView) findViewById(R.id.all_mgzs);
        if (mRecentlyReads == null) return;
        int count = mRecentlyReads.size();
        for (int i = 0; i < count; i ++) {
            BookshelfItem item = mRecentlyReads.get(i);
            mGridView.addView(item.getView(), mItemWidth,mItemHeight);
        }
    }
    
    private ArrayList<BookshelfItem> getRecentlyReads() {
        ArrayList<BookshelfItem> list = new ArrayList<BookshelfItem>();
        Cursor paths = getRecentlyReadsPath();
        if (paths != null) {
            int count = paths.getCount();
            for (int i = 0; i < count; i ++) {
                String path = paths.getString(COLUMN_INDEX_RECENTLY_READS_PATH);
                BookshelfItem item = new BookshelfItem(this, path);
                list.add(item);
            }
        }
        
        //TEST
        for (int i = 0; i < 9; i ++) {
            String path = Environment.getExternalStorageDirectory().toString() + "/test.jpg";
            BookshelfItem item = new BookshelfItem(this, path);
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
                BookshelfItem item = new BookshelfItem(this, path);
                list.add(item);
            }
        }
        return null;
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
}

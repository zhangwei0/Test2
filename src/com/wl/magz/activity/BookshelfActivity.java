package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.view.AllMgzsAdapter;
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
    
    private GridView mRecentGridView;
    private GridView mAllGridView;
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
        mItemHeight = (int) (display.heightPixels / 5);
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
        if (mRecentlyReads == null) return;

        AllMgzsAdapter adapter = new AllMgzsAdapter(this, mAllMgzs);
        mAllGridView.setAdapter(adapter);
        mAllGridView.setNumColumns(3);
    }
    
    private ArrayList<BookshelfItem> getRecentlyReads() {
        ArrayList<BookshelfItem> list = new ArrayList<BookshelfItem>();
        Cursor paths = getRecentlyReadsPath();
        if (paths != null) {
            int count = paths.getCount();
            for (int i = 0; i < count; i ++) {
                String path = paths.getString(COLUMN_INDEX_RECENTLY_READS_PATH);
                BookshelfItem item = new BookshelfItem(this, path, BookshelfItem.TYPE_RECENTLY_READS, mItemWidth, mItemHeight);
                list.add(item);
            }
        }
        
        //TEST
        for (int i = 0; i < 3; i ++) {
            String path = Environment.getExternalStorageDirectory().toString() + "/test2.jpg";
            BookshelfItem item = new BookshelfItem(this, path, BookshelfItem.TYPE_RECENTLY_READS, mItemWidth, mItemHeight);
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
                BookshelfItem item = new BookshelfItem(this, path, BookshelfItem.TYPE_ALL_MGZS, mItemWidth, mItemHeight);
                list.add(item);
            }
        }
        
      //TEST
        for (int i = 0; i < 100; i ++) {
            String path = Environment.getExternalStorageDirectory().toString() + "/test.jpg";
            BookshelfItem item = new BookshelfItem(this, path, BookshelfItem.TYPE_RECENTLY_READS, mItemWidth, mItemHeight);
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
}

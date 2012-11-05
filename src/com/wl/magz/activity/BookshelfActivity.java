package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.LinearLayout;

public class BookshelfActivity extends Activity {

    private ArrayList<?> mRecentlyReads;
    private ArrayList<?> mAllMgzs;
    
    private LinearLayout mLinearLayout;
    private GridView mGridView;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.bookshelf);
        
        //These two may be run in background
        mRecentlyReads = getRecentlyReads();
        mAllMgzs = getAllMgzs();
        
        initViews();
    }
    
    private void initViews() {
        mLinearLayout = (LinearLayout) findViewById(R.id.recently_reads);
        mGridView = (GridView) findViewById(R.id.all_mgzs);
    }
    
    private ArrayList<?> getRecentlyReads() {
        Cursor paths = getRecentlyReadsPath();
        
        //TODO
        //Load images though the 'paths'
//        ArrayList<? /* which type? */> list = new ArrayList<?>();
        return null;
    }
    
    private ArrayList<?> getAllMgzs() {
        Cursor paths = getAllMgzsPath();
        
        //TODO
        //Load images though the 'path'
        //ArrayList<? /* which type */> list = new ArrayList<?>();
        
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

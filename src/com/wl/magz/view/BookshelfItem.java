package com.wl.magz.view;

import com.wl.magz.utils.Constant.DBConstant;

import android.content.ContentValues;
import android.database.Cursor;

public class BookshelfItem {
    
    public long mId;
    protected String mPrivateKey;
    public String mName;
    public int mProgress;
    public boolean mDownloadComplete;
    public long mReadTime;
    public Object mData;
        
    private static final int INDEX_ID = 0;
    private static final int INDEX_PRIVATE_KEY = 1;
    private static final int INDEX_NAME = 2;
    private static final int INDEX_PATH = 3;
    private static final int INDEX_DOWNLOAD_COMPLETE = 4;
    private static final int INDEX_PROGRESS = 5;
    private static final int INDEX_READ_TIME = 6;
    
    public BookshelfItem() {}
    
    public BookshelfItem(String key) {
        mPrivateKey = key;
    }

    public ContentValues toValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBConstant.PRIVATE_KEY, mPrivateKey);
        cv.put(DBConstant.NAME, mName);
        
        //TODO
        cv.put(DBConstant.PATH, (String) mData);
        cv.put(DBConstant.DOWNLOAD_COMPLETE, mDownloadComplete);
        cv.put(DBConstant.PROGRESS, mProgress);
        cv.put(DBConstant.READ_TIME, mReadTime);
        return cv;
    }

    public void initFromCursor(Cursor c) {
        mId = c.getLong(INDEX_ID);
        mPrivateKey = c.getString(INDEX_PRIVATE_KEY);
        mName = c.getString(INDEX_NAME);
        mData = c.getString(INDEX_PATH);
        mDownloadComplete = ((c.getInt(INDEX_DOWNLOAD_COMPLETE) == 0) ? false : true);
        mProgress = c.getInt(INDEX_PROGRESS);
        mReadTime = c.getInt(INDEX_READ_TIME);
    }

}

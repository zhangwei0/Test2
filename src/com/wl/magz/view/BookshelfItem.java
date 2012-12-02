package com.wl.magz.view;

import android.content.ContentValues;
import android.database.Cursor;

public abstract class BookshelfItem {
    protected static final int TYPE_ALL = 0;
    protected static final int TYPE_RECENT = 1;
    
    protected int mPrivateKey;
    public int mType;
    public long mId;
    
    public int mProgress;
    public boolean mInProgress;

    public Object mData;
    
    public BookshelfItem() {}
    
    public BookshelfItem(int key) {
        mPrivateKey = key;
    }

    protected int getType() {
        return mType;
    }
    
    public abstract ContentValues toValues();
    public abstract void initFromCursor(Cursor c);
    
    public static class DownloadItem extends BookshelfItem {
        public DownloadItem() {}
        public DownloadItem(int key) {
            super(key);
            mType = TYPE_ALL;
        }

        public boolean mDownloadComplete = true;
        public int mProgress;
        @Override
        public ContentValues toValues() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void initFromCursor(Cursor c) {
            // TODO Auto-generated method stub
            
        }

    }
    
    public static class RecentItem extends BookshelfItem {
        public RecentItem() {}
        public RecentItem(int key) {
            super(key);
            mType = TYPE_RECENT;
        }
        
        public long mReadTime;

        @Override
        public ContentValues toValues() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void initFromCursor(Cursor c) {
            // TODO Auto-generated method stub
            
        }
    }
}

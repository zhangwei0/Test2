package com.wl.magz.view;

import android.graphics.Bitmap;

import com.wl.magz.view.BookshelfItemView;

public class BookshelfItem{
    
    public static final int TYPE_RECENTLY_READS = 0;
    public static final int TYPE_ALL_MGZS = 1;

    public long mId;
    public int mType;
    public int mProgress;
    public boolean mInProgress;
    public Object mData;
    
    public Bitmap mBitmap;
    public boolean mLoading;
//    public boolean mComplete;
    
    public BookshelfItemView mView;

    public BookshelfItem(int type, Object data, long id) {
        if ((type != TYPE_RECENTLY_READS) && (type != TYPE_ALL_MGZS)) {
            throw new IllegalStateException();
        }
        mType = type;
        mData = data;
        mId = id;
    }


    public BookshelfItem(String path, long id) {
        this(TYPE_RECENTLY_READS, path, id);
    }
}

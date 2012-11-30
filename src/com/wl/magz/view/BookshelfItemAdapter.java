package com.wl.magz.view;

import java.util.ArrayList;

import com.wl.magz.utils.ImageWorker;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.util.Log;

public class BookshelfItemAdapter extends BaseAdapter {
    public static final int TYPE_ALL = 0;
    public static final int TYPE_RECENTS = 1;
    private Context mContext;
    private Cursor mData;
    private ArrayList<BookshelfItem> mData2;
    private int mType = 0;
    private ImageWorker mImageWorker;
    public BookshelfItemAdapter(Context context, ImageWorker imageWorker, Cursor c) {
        mContext = context;
        mImageWorker = imageWorker;
        mData = c;
        BookshelfItemView.setImageWorker(imageWorker);
    }
    
    public BookshelfItemAdapter(Context context, ImageWorker worker, Cursor c, ArrayList<BookshelfItem> paths) {
        this(context, worker, null);
        mData2 = paths;
    }

    public int getCount() {
        return mData2.size();
    }

    public Object getItem(int i) {
        return mData2.get(i);
    }

    public long getItemId(int position) {
        return ((BookshelfItem)getItem(position)).mId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        BookshelfItem item = (BookshelfItem)mData2.get(position);
        BookshelfItemView itemView;
        if (convertView  == null) {
            itemView = BookshelfItemView.newView(mContext, item);
        } else {
            itemView = BookshelfItemView.fromView(mContext, convertView, item);
        }
        return itemView.getView();
    }
}

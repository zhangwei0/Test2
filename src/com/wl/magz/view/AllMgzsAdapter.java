package com.wl.magz.view;

import java.util.ArrayList;

import com.wl.magz.data.BookshelfItem;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.util.Log;

public class AllMgzsAdapter extends BaseAdapter{
    private Context mContext;
    private ArrayList<BookshelfItem> mData;
    public AllMgzsAdapter(Context context, ArrayList<BookshelfItem> data) {
        mContext = context;
        mData = data;
    }

    public int getCount() {
        return mData.size();
    }

    public Object getItem(int i) {
        return mData.get(i);
    }

    public long getItemId(int position) {
        return ((BookshelfItem)getItem(position)).mId;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Log.e("Adaptrer", "pos:" + position);
        BookshelfItem item = (BookshelfItem)mData.get(position);
        BookshelfItemView itemView;
        if (convertView  == null) {
            Log.e("Adapter", "new");
            itemView = BookshelfItemView.newView(mContext, item);
        } else {
            Log.e("Adapter", "old");
            itemView = BookshelfItemView.fromView(mContext, convertView, item);
        }

        return itemView.getView();
    }

}

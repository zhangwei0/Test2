package com.wl.magz.view;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

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
        return ((BookshelfItem)getItem(position)).getId();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        //TODO
        if (convertView  == null) {
            convertView = ((BookshelfItem)getItem(position)).getView();
        } else {
            
        }
        
        return convertView;
    }

}

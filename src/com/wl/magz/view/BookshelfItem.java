package com.wl.magz.view;

import com.wl.magz.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BookshelfItem{
    
    private static LayoutInflater mInflater;

    private LinearLayout mView;
    private ImageView mImage;
    private ProgressBar mProgress;
    private TextView mPercentText;
    public BookshelfItem(Context context) {
        init(context);
    }
    
    private void init(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        mView = (LinearLayout) mInflater.inflate(R.layout.bookshelf_item, null);
        //TODO 
        init..
    }

}

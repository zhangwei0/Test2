package com.wl.magz.view;

import com.wl.magz.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BookshelfItem{
    
    private Context mContext;
    private static LayoutInflater mInflater;

    private LinearLayout mView;
    private FrameLayout mFrame;
    private LinearLayout mProgressView;
    private ImageView mImage;
//    private ImageView mLoadImage;
    private ProgressBar mProgress;
    private TextView mProgressText;
    
    private boolean mInProgress = false;
    public BookshelfItem(Context context) {
        mContext = context;
        init(context);
        checkState();
    }
    
    public BookshelfItem(Context context, String path) {
        this(context);
        setImagePath(path);
    }
    
    public BookshelfItem(Context context, String path, boolean inProgress) {
        
    }
    
    private void init(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        
        mView = (LinearLayout) mInflater.inflate(R.layout.bookshelf_item, null);
        mFrame = (FrameLayout) mView.findViewById(R.id.content_layout);
        mProgressView = (LinearLayout) mView.findViewById(R.id.progress_view);
        mImage = (ImageView) mView.findViewById(R.id.image);
 //       mLoadImage = (ImageView) mView.findViewById(R.id.load_image);
        mProgress = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mProgressText = (TextView) mView.findViewById(R.id.progress_text);
//        mLoadImage.setVisibility(View.GONE);
        mFrame.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                Toast.makeText(mContext, "Click", Toast.LENGTH_LONG).show();
            }
            
        });
    }
    
    public void setImagePath(String path) {
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        if (bitmap == null) return;
        setImageBitmap(bitmap);
    }
    
    public void setImageBitmap(Bitmap bitmap) {
        mImage.setImageBitmap(bitmap);
    }
    
    public void setProgress(int progress) {
        mProgress.setProgress(progress);
        mProgressText.setText(progress + "%");
    }
    
    public void setProgressVisible(boolean visible) {
        mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
    
    public View getView() {
        return mView;
    }
    
    public void setState(boolean inProgress) {
        if (inProgress != mInProgress) {
            mInProgress = inProgress;
            checkState();
        }
    }
    
    private void checkState() {
        if (mInProgress) {
            mImage.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);
        } else {
            mImage.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }
    }

}

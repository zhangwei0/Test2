package com.wl.magz.view;

import com.wl.magz.R;
import com.wl.magz.utils.ImageWorker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BookshelfItemView{
    
    public static final int TYPE_RECENTLY_READS = 0;
    public static final int TYPE_ALL_MGZS = 1;
    
    private Context mContext;
    private static LayoutInflater mInflater;

    private LinearLayout mView;
    private FrameLayout mFrame;
    private LinearLayout mProgressView;
    private ImageView mImage;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    
    public static int mImageWidth;
    public static int mImageHeight;
    
    public long mId;
    private int mType;
    private boolean mInProgress = false;
    private int mProgress;
    
    public BookshelfItem mItem;
    private Object mData;
    
    public static ImageWorker mImageWorker;

    private BookshelfItemView(Context context) {
        mContext = context;
    };
    
    public static void setImageWorker(ImageWorker imageWorker) {
        mImageWorker = imageWorker;
    }
    
    public static BookshelfItemView newView(Context context, BookshelfItem item) {
        BookshelfItemView me = new BookshelfItemView(context);
        me.initData(item);
        me.initView(context);
        me.mView.setTag(me);
        me.mImage.setImageBitmap(null);
        me.loadImage();
        me.checkState();
        return me;
    }
    
    public static BookshelfItemView fromView(Context context, View v, BookshelfItem item) {
        BookshelfItemView me = (BookshelfItemView) v.getTag();
        me.initData(item);
        me.checkState();
        me.mImage.setImageBitmap(null);
        me.loadImage();
        return me;
    }
    
    private void loadImage() {
        mImageWorker.loadImage(mImage, mData);
    }
    
    private void initData(BookshelfItem item) {
        item.mView = this;
        this.mItem = item;
        this.mType = item.mType;
        this.mId = item.mId;
        this.mInProgress = item.mInProgress;
        this.mProgress = item.mProgress;
        this.mData = item.mData;
    }
    
    private void initView(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mView = (LinearLayout) mInflater.inflate(R.layout.bookshelf_item, null);

        mFrame = (FrameLayout) mView.findViewById(R.id.content_layout);
        mProgressView = (LinearLayout) mView.findViewById(R.id.progress_view);
        mImage = (ImageView) mView.findViewById(R.id.image);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
        mProgressText = (TextView) mView.findViewById(R.id.progress_text);

        mFrame.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                Toast.makeText(mContext, "Click", Toast.LENGTH_LONG).show();
            }
            
        });
    }
    
    public void setProgress(int progress) {
        
        mProgressBar.setProgress(progress);
        mProgressText.setText(progress + "%");
    }
    
    public void setProgressVisible(boolean visible) {
        mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
    
    public View getView() {
        return mView;
    }
    
    public void setState(boolean inProgress) {
        if (mType == TYPE_RECENTLY_READS) {
            return;
        }
        if (inProgress != mInProgress) {
            mInProgress = inProgress;
            checkState();
        }
    }
    
    private void checkState() {
        if (mType == TYPE_RECENTLY_READS) {
            mImage.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        } else if (mInProgress) {
            mImage.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);
            setProgress(mProgress);
        } else {
            mImage.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }
    }
}

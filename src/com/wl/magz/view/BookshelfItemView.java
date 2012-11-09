package com.wl.magz.view;

import com.wl.magz.R;
import com.wl.magz.data.BookshelfItem;

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

public class BookshelfItemView{
    
    public static final int TYPE_RECENTLY_READS = 0;
    public static final int TYPE_ALL_MGZS = 1;
    
    private Context mContext;
    private static LayoutInflater mInflater;

    private LinearLayout mView;
    private FrameLayout mFrame;
    private LinearLayout mProgressView;
    private ImageView mImage;
//    private ImageView mLoadImage;
    private ProgressBar mProgressBar;
    private TextView mProgressText;
    
    public static int mImageWidth;
    public static int mImageHeight;
    
    public long mId;
    private int mType;
    private boolean mInProgress = false;
    private int mProgress;
    
    public BookshelfItem mItem;
    private String mPath;
    public Bitmap mBitmap;
    
    public interface Callback {
        public void loadAndSetImage(String path, BookshelfItemView itemView, BookshelfItem item);
    }
    
    private static Callback mCallback;
    public static void setCallback(Callback callback) {
        mCallback = callback;
    }
    
    private BookshelfItemView() {};
    
    public static BookshelfItemView newView(Context context, BookshelfItem item) {
        BookshelfItemView me = new BookshelfItemView();
        item.mView = me;
        me.mContext = context;
        me.mItem = item;
        me.mType = item.mType;
        me.mId = item.mId;
        me.mInProgress = item.mInProgress;
        me.mProgress = item.mProgress;
        me.mPath = item.mPath;
        me.mBitmap = item.mBitmap;
        me.initView(context);
        me.mView.setTag(me);
        me.clearImage();
        if ((item.mBitmap == null) && (!item.mLoading)) {
            item.mLoading = true;
            mCallback.loadAndSetImage(item.mPath, me, item);
        } else if(item.mBitmap != null){
            item.mLoading = false;
            me.setImageBitmap(item.mBitmap);
        }
        me.checkState();
        return me;
    }
    
    public static BookshelfItemView fromView(Context context, View v, BookshelfItem item) {
        BookshelfItemView me = (BookshelfItemView) v.getTag();
        item.mView = me;
        me.mItem = item;
        me.mType = item.mType;
        me.mId = item.mId;
        me.mInProgress = item.mInProgress;
        me.mProgress = item.mProgress;
        me.mPath = item.mPath;
        me.mBitmap = item.mBitmap;
        me.clearImage();
        if (item.mBitmap == null && (!item.mLoading)) {
            item.mLoading = true;
            mCallback.loadAndSetImage(item.mPath, me, item);
        } else if(item.mBitmap != null){
            item.mLoading = false;
            me.setImageBitmap(item.mBitmap);
        }
        me.checkState();
        return me;
    }
    
    private void clearImage() {
        mImage.setImageBitmap(null);
    }
    
    private void initView(Context context) {
        if (mInflater == null) {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        mView = (LinearLayout) mInflater.inflate(R.layout.bookshelf_item, null);
    //    getViewFromParent(mView);
        
        mFrame = (FrameLayout) mView.findViewById(R.id.content_layout);
        mProgressView = (LinearLayout) mView.findViewById(R.id.progress_view);
        mImage = (ImageView) mView.findViewById(R.id.image);
 //       mLoadImage = (ImageView) mView.findViewById(R.id.load_image);
        mProgressBar = (ProgressBar) mView.findViewById(R.id.progress_bar);
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

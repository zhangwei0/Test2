package com.wl.magz.view;

import com.wl.magz.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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
    
    public static final int TYPE_RECENTLY_READS = 0;
    public static final int TYPE_ALL_MGZS = 1;
    
    private Context mContext;
    private static LayoutInflater mInflater;

    private LinearLayout mView;
    private FrameLayout mFrame;
    private LinearLayout mProgressView;
    private ImageView mImage;
//    private ImageView mLoadImage;
    private ProgressBar mProgress;
    private TextView mProgressText;
    
    public static int mImageWidth;
    public static int mImageHeight;
    
    private int mId;
    private int mType;
    private boolean mInProgress = false;
    
    public interface Callback {
        public void loadAndSetImage(String path, BookshelfItem item);
    }
    
    private static Callback mCallback;
    public static void setCallback(Callback callback) {
        mCallback = callback;
    }
    private BookshelfItem(Context context) {
        mContext = context;
        init(context);
    }
    
    private BookshelfItem(Context context, int type) {
        this(context);
        if ((type != TYPE_RECENTLY_READS) && (type != TYPE_ALL_MGZS)) {
            throw new IllegalStateException();
        }
        mType = type;
        checkState();
    }
    
    public BookshelfItem(Context context, String path, int type) {
        this(context, type);
        if (path != null) {
            setImagePath3(path);
        }
    }

    public BookshelfItem(Context context, String path, boolean inProgress) {
        this(context, path, TYPE_RECENTLY_READS);
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
    
    //TODO
    //This will be run in background
    private void setImagePath2(String path){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bm = BitmapFactory.decodeFile(path,option);
        if (bm == null) return;
        Bitmap newBm = zoom(bm);
        setImageBitmap(newBm);
    }
    
    private Bitmap zoom(Bitmap bm) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        int newWidth = mImageWidth;
        int newHeight = mImageHeight;
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newBm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
        return newBm;
    }
    
    private void setImagePath3(String path) {
        if (mCallback != null) {
             mCallback.loadAndSetImage(path, this);
        }
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
        } else {
            mImage.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }
    }
    
    public int getId() {
        return mId;
    }
    
    public void setId(int id) {
        mId = id;
    }

}

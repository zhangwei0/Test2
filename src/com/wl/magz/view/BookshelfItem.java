package com.wl.magz.view;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
    
    private int mImageWidth;
    private int mImageHeight;
    
    private int mId;
    private int mType;
    private boolean mInProgress = false;
    private BookshelfItem(Context context, int imageWidth, int imageHeight) {
        mContext = context;
        mImageWidth = imageWidth;
        mImageHeight = imageHeight;
        init(context);
    }
    
    private BookshelfItem(Context context, int type, int imageWidth, int imageHeight) {
        this(context,imageWidth, imageHeight);
        if ((type != TYPE_RECENTLY_READS) && (type != TYPE_ALL_MGZS)) {
            throw new IllegalStateException();
        }
        mType = type;
        checkState();
    }
    
    public BookshelfItem(Context context, String path, int type, int imageWidth, int imageHeight) {
        this(context, type, imageWidth, imageHeight);
        if (path != null) {
            setImagePath2(path);
        }
    }

    public BookshelfItem(Context context, String path, boolean inProgress, int imageWidth, int imageHeight) {
        this(context, path, TYPE_RECENTLY_READS, imageWidth, imageHeight);
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
    
    private void setImagePath2(String path){
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2; //将图片设为原来宽高的1/2，防止内存溢出
        Bitmap bm = BitmapFactory.decodeFile(path,option);//文件流
        if (bm == null) return;
        setImageBitmap(zoom(bm));
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

package com.wl.magz.view;

import com.wl.magz.R;
import com.wl.magz.utils.DBHelper;
import com.wl.magz.utils.ImageWorker;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    
    public long mId;
    private boolean mDownloadComplete = true;
    private int mProgress;
    
    public BookshelfItem mItem;
    private Object mData;
    @SuppressWarnings("unused")
    private long mReadTime;
    
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
        this.mItem = item;
        this.mId = item.mId;
        this.mDownloadComplete = item.mDownloadComplete;
        this.mProgress = item.mProgress;
        this.mData = item.mData;
        this.mReadTime = item.mReadTime;
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
                if (!mDownloadComplete) {
                    showCancelDialog(mId);
                } else {
                    //TODO
                    //Open magz
                }
            }
            
        });
        
        mFrame.setOnLongClickListener(new OnLongClickListener() {

            public boolean onLongClick(View arg0) {
                if (!mDownloadComplete) {
                    showCancelDialog(mId);
                } else {
                    showDeleteDialog(mId);
                }
                return true;
            }
            
        });
    }
    
    private void showDeleteDialog(long id) {
        DeleteListener l = new DeleteListener(id);
        Builder dialog =  new AlertDialog.Builder(mContext)
//        .setTitle(R.string.no_sdcard_title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(R.string.delete_magz)
        .setPositiveButton(android.R.string.ok, l)
        .setNegativeButton(android.R.string.cancel, null);
        dialog.show();
    }
    
    private void showCancelDialog(long id) {
        CancelListener l = new CancelListener(id);
        Builder dialog =  new AlertDialog.Builder(mContext)
//        .setTitle(R.string.no_sdcard_title)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setMessage(R.string.cancel_download)
        .setPositiveButton(android.R.string.ok, l)
        .setNegativeButton(android.R.string.cancel, null);
        dialog.show();
    }
    
    private class DeleteListener implements android.content.DialogInterface.OnClickListener {
        private long mId;

        public DeleteListener(long id) {
            mId = id;
        }
        
        public void onClick(DialogInterface dialog, int which) {
            DBHelper.deleteMyMagz(mId);
            //TODO
            //StoreManager.deleteMyMagz(mId);
        }
    }
    
    private class CancelListener implements android.content.DialogInterface.OnClickListener {
        private long mId;
        public CancelListener(long id) {
            mId = id;
        }

        public void onClick(DialogInterface dialog, int which) {
            DBHelper.deleteMyMagz(mId);
            //TODO
            //DownloadManager.cancelDownload(mId);
            //StoreManager.deleteMyMagz(mId);
        }
        
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
    
    public void setState(boolean downloadComplete) {
        if (downloadComplete != mDownloadComplete) {
            mDownloadComplete = downloadComplete;
            checkState();
        }
    }
    
    private void checkState() {
        if (mDownloadComplete) {
            mImage.setVisibility(View.GONE);
            mProgressView.setVisibility(View.VISIBLE);
            setProgress(mProgress);
        } else {
            mImage.setVisibility(View.VISIBLE);
            mProgressView.setVisibility(View.GONE);
        }
    }
}

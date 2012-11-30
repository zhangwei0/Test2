package com.wl.magz.utils;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public abstract class ImageWorker {
    private static final String TAG = "ImageWorker";
    private static final int FADE_IN_TIME = 200;

    protected Context mContext;
    private Bitmap mLoadingBitmap;
    private ImageCache mImageCache;
    public boolean mExitTaskEarly = false;

    public ImageWorker(Context context) {
        mContext = context;
    }
    public void setLoadingBitmapResource(int resId) {
        mLoadingBitmap = BitmapFactory.decodeResource(mContext.getResources(), resId);
    }
    
    public void loadImage(ImageView imageView, Object data) {
        Log.e(TAG, "loadImage");
        Bitmap b = null;
        if (mImageCache != null) {
            Log.e(TAG, "loadImage_getBitmapFromMemCache");
            b = mImageCache.getBitmapFromMemCache(String.valueOf(data));
        }
        if (b != null) {
            Log.e(TAG, "loadImage_setImageBitmap");
            setImageBitmap(imageView, b);
        } else if (cancelPotentialTask(imageView, data)){
            Log.e(TAG, "new LoadAsyncTask");
            LoadAsyncTask task = new LoadAsyncTask(imageView);
            LoadDrawable drawable = new LoadDrawable(mLoadingBitmap, task);
            setImageDrawable(imageView, drawable);
            task.execute(data);
        }
    }
    
    private boolean cancelPotentialTask(ImageView imageView, Object data) {
        LoadAsyncTask task = getAttachedTask(imageView, data);
        if (task != null) {
            Object taskData = task.data;
            if (taskData == null || !task.equals(data)) {
                task.cancel(true);
            } else {
                return false;
            }
        }
        return true;
    }
    
    private static LoadAsyncTask getAttachedTask(ImageView imageView, Object data) {
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof LoadDrawable) {
                return ((LoadDrawable) drawable).task.get();
            }
        }
        return null;
    }
    
    private void setImageBitmap(ImageView imageView, Bitmap b) {
        imageView.setImageBitmap(b);
    }
    
    private void setImageBitmap2(ImageView imageView, Bitmap bitmap) {
        if (true) {
            // Transition drawable with a transparent drwabale and the final bitmap
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(android.R.color.transparent),
                            new BitmapDrawable(mContext.getResources(), bitmap)
                    });
            // Set background to loading bitmap
            imageView.setBackgroundDrawable(
                    new BitmapDrawable(mContext.getResources(), mLoadingBitmap));

            imageView.setImageDrawable(td);
            td.startTransition(FADE_IN_TIME);
        }
    }
    
    private void setImageDrawable(ImageView imageView, LoadDrawable drawable) {
        imageView.setImageDrawable(drawable);
    }
    
    protected abstract Bitmap processBitmap(Object data);
    
    private class LoadAsyncTask extends AsyncTask<Object, Void, Bitmap> {
        Object data;
        WeakReference<ImageView> imageView;
        public LoadAsyncTask(ImageView imageView) {
            this.imageView = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected void onCancelled() {
            // TODO Auto-generated method stub
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Bitmap result) {
//            Log.e(TAG, "onPostExecute");
            if (isCancelled() || mExitTaskEarly) {
                result = null;
            }
            if (result != null) {
                ImageView image = getAttachedImageView();
                LoadAsyncTask task = getAttachedTask(image, null);
                if (task != null && task.equals(this)) {
                    setImageBitmap2(image, result);
                }
            }
        }

        @Override
        protected Bitmap doInBackground(Object... params) {
//            Log.e(TAG, "doInBackgroud");
            data = params[0];
            String dataString = String.valueOf(data);
            Bitmap bitmap = null;
            if (mImageCache != null && !isCancelled() && !mExitTaskEarly && getAttachedImageView() != null) {
                bitmap = mImageCache.getBitmapFromDiskCache(dataString);
            }
            
            if (bitmap == null && !isCancelled() && !mExitTaskEarly && getAttachedImageView() != null) {
                bitmap = processBitmap(params[0]);
            }
            
            if (mImageCache != null && bitmap != null) {
                mImageCache.addBitmapToCache(dataString, bitmap);
            }
            
            return bitmap;
        }
        
        private ImageView getAttachedImageView() {
            final ImageView image = imageView.get();
            final LoadAsyncTask task = getAttachedTask(image, null);
            if (this == task) {
                return image;
            }
            return null;
        }
        
    }
    
    private class LoadDrawable extends BitmapDrawable {
        public LoadDrawable(Bitmap drawable, LoadAsyncTask task) {
            super(drawable);
            this.task = new WeakReference<LoadAsyncTask>(task);
        }

        WeakReference<LoadAsyncTask> task;
    }

    public void setImageCache(ImageCache imageCache) {
        mImageCache = imageCache;
        
    }
}

package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.utils.DBHelper;
import com.wl.magz.utils.ImageCache;
import com.wl.magz.utils.ImageCache.ImageCacheParams;
import com.wl.magz.utils.ImageResizer;
import com.wl.magz.utils.ImageWorker;
import com.wl.magz.utils.Utils;
import com.wl.magz.view.BookshelfItem;
import com.wl.magz.view.BookshelfItemAdapter;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class BookshelfFragment extends Fragment {
    public static final String TAG = "BookshelfFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    private BookshelfItemAdapter mAllAdapter;
    private BookshelfItemAdapter mRecentsAdapter;
    
    private ImageWorker mImageWorker;
    
    ArrayList<BookshelfItem> mMyItems = new ArrayList<BookshelfItem>();
    ArrayList<BookshelfItem> mRecentItems = new ArrayList<BookshelfItem>();

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        initData();

        int[] size = getItemShape();
        mImageWorker = new ImageResizer(getActivity(), size[0], size[1]);
        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
        cacheParams.memCacheSize = 1024 * 1024 * Utils.getMemoryClass(getActivity()) / 3;
        mImageWorker.setLoadingBitmapResource(R.drawable.cover_temp);
        mImageWorker.setImageCache(ImageCache.findOrCreateCache(getActivity(), cacheParams));
        
        mAllAdapter = new BookshelfItemAdapter(getActivity(), mImageWorker, null, mMyItems);
        mRecentsAdapter = new BookshelfItemAdapter(getActivity(), mImageWorker, null, mRecentItems);
    }
    
    private int[] getItemShape() {
        DisplayMetrics display = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        int imageWidth = display.widthPixels / 3;
        int imageHeight = (int) (display.heightPixels / 5);
        return new int[] {imageWidth, imageHeight};
    }
    
    private void initData() {
        Cursor allCursor = DBHelper.getMyMagzs();
        Cursor recentsCursor = DBHelper.getRecentReads();

        mMyItems = new ArrayList<BookshelfItem>();
        mRecentItems = new ArrayList<BookshelfItem>();
        if (allCursor != null) {
            allCursor.moveToPosition(-1);
            while (allCursor.moveToNext()) {
                BookshelfItem magz = new BookshelfItem();
                magz.initFromCursor(allCursor);
                mMyItems.add(magz);
            }
        }
        
        if (recentsCursor != null) {
            recentsCursor.moveToPosition(-1);
            while (recentsCursor.moveToNext() && mRecentItems.size() < 3) {
                BookshelfItem magz = new BookshelfItem();
                magz.initFromCursor(recentsCursor);
                mRecentItems.add(magz);
            }
        }
    }
    
    @Override
    public View onCreateView(LayoutInflater flater, ViewGroup group, Bundle bundle) {
        View v = flater.inflate(R.layout.bookshelf, group, false);
        GridView recents = (GridView) v.findViewById(R.id.recently_reads);
        GridView all = (GridView) v.findViewById(R.id.all_mgzs);
        all.setAdapter(mAllAdapter);
        recents.setAdapter(mRecentsAdapter);
        recents.setNumColumns(3);
        all.setNumColumns(3);
        return v;
    }
}

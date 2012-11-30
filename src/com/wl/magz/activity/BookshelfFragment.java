package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.utils.ImageCache;
import com.wl.magz.utils.ImageCache.ImageCacheParams;
import com.wl.magz.utils.ImageResizer;
import com.wl.magz.utils.ImageWorker;
import com.wl.magz.utils.Utils;
import com.wl.magz.view.BookshelfItem;
import com.wl.magz.view.BookshelfItemAdapter;

import android.app.Activity;
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

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
//        Cursor allCursor = Utils.getAllMgzsPath();
//        Cursor recentsCursor = Utils.getRecentlyReadsPath();
        
        ArrayList<BookshelfItem> all = new ArrayList<BookshelfItem>();
        for (int i = 1; i < 16; i ++) {
            String path = "/sdcard/test" + i + ".jpg";
            BookshelfItem item = new BookshelfItem(path, 0);
            all.add(item);
        }
        
        ArrayList<BookshelfItem> recents = new ArrayList<BookshelfItem>();
        for (int i = 1; i < 4; i ++) {
            String path = "/sdcard/test" + i + ".jpg";
            BookshelfItem item = new BookshelfItem(path, 0);
            recents.add(item);
        }
        
        int[] size = getItemShape();
        mImageWorker = new ImageResizer(getActivity(), size[0], size[1]);
        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
        cacheParams.memCacheSize = 1024 * 1024 * Utils.getMemoryClass(getActivity()) / 3;
        mImageWorker.setLoadingBitmapResource(R.drawable.cover_temp);
        mImageWorker.setImageCache(ImageCache.findOrCreateCache(getActivity(), cacheParams));
        
        mAllAdapter = new BookshelfItemAdapter(getActivity(), mImageWorker, null, all);
        mRecentsAdapter = new BookshelfItemAdapter(getActivity(), mImageWorker, null, recents);
    }
    
    private int[] getItemShape() {
        DisplayMetrics display = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(display);
        int imageWidth = display.widthPixels / 3;
        int imageHeight = (int) (display.heightPixels / 5);
        return new int[] {imageWidth, imageHeight};
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

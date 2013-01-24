package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.view.AllInOneAdapter;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AllInOneFragment extends Fragment {
    public static final String TAG = "AllInOneFragment";
    
    private FragmentManager mFm;
    private Fragment mBookshelfFragment;
    private Fragment mMagzFragment;
    private Fragment mMagzFragment2;
    private ArrayList<Fragment> mFragments;

    private ViewPager mViewPager;
    private AllInOneAdapter mPagerAdapter;
    private TextView mTab1;
    private TextView mTab2;
    private TextView mTab3;
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        
        mFm = getActivity().getSupportFragmentManager();
        mBookshelfFragment = mFm.findFragmentByTag(BookshelfFragment.TAG);
        mMagzFragment = mFm.findFragmentByTag(MagzFragment.TAG);
        mMagzFragment2 = mFm.findFragmentByTag(MagzFragment2.TAG);
        if (mBookshelfFragment == null) {
            mBookshelfFragment = new BookshelfFragment();
        }
        if (mMagzFragment == null) {
            mMagzFragment = new MagzFragment();
        }
        if (mMagzFragment2 == null) {
            mMagzFragment2 = new MagzFragment2();
        }

        mFragments = new ArrayList<Fragment>();
        mFragments.add(mBookshelfFragment);
        mFragments.add(mMagzFragment);
        mFragments.add(mMagzFragment2);
        
        mPagerAdapter = new AllInOneAdapter(getActivity());
        mPagerAdapter.setFragments(mFragments);
    }
    
    public View onCreateView(LayoutInflater flater, ViewGroup vp, Bundle bundle) {
        View v = flater.inflate(R.layout.all_in_one, vp, false);
        mViewPager = (ViewPager) v.findViewById(R.id.view_pager);
        mTab1 = (TextView) v.findViewById(R.id.tab1);
        mTab2 = (TextView) v.findViewById(R.id.tab2);
        mTab3 = (TextView) v.findViewById(R.id.tab3);
        TabSelectListener selector = new TabSelectListener();
        mTab1.setOnClickListener(selector);
        mTab2.setOnClickListener(selector);
        mTab3.setOnClickListener(selector);

        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        mViewPager.setOnPageChangeListener(new PageChangeListener());
        return v;
    }
    
    private class TabSelectListener implements OnClickListener {

        public void onClick(View v) {
            switch (v.getId()) {
            case R.id.tab1:
                mViewPager.setCurrentItem(0);
                break;
                
            case R.id.tab2:
                mViewPager.setCurrentItem(1);
                break;
                
            case R.id.tab3:
                mViewPager.setCurrentItem(2);
                break;
                
            }
            
        }
        
    }
    
    private class PageChangeListener implements OnPageChangeListener {

        public void onPageSelected(int arg0) {

            switch (arg0) {
            case 0:
                mTab1.setTextColor(Color.BLACK);
                mTab2.setTextColor(Color.GRAY);
                mTab3.setTextColor(Color.GRAY);
                break;
            case 1:
                mTab2.setTextColor(Color.BLACK);
                mTab1.setTextColor(Color.GRAY);
                mTab3.setTextColor(Color.GRAY);
                break;
            case 2:
                mTab3.setTextColor(Color.BLACK);
                mTab2.setTextColor(Color.GRAY);
                mTab1.setTextColor(Color.GRAY);
                break;
            }

        }


        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }


        public void onPageScrollStateChanged(int arg0) {
        }
    }
}

package com.wl.magz.activity;

import java.util.ArrayList;

import com.wl.magz.R;
import com.wl.magz.view.AllInOneAdapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AllInOneFragment extends Fragment {
    public static final String TAG = "AllInOneFragment";
    
    private FragmentManager mFm;
    private Fragment mBookshelfFragment;
    private Fragment mMagzFragment;
    private Fragment mMagzFragment2;
    private ArrayList<Fragment> mFragments;

    private ViewPager mViewPager;
    private AllInOneAdapter mPagerAdapter;
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

        FragmentTransaction ft = mFm.beginTransaction();
        ft.add(R.id.view_pager, mBookshelfFragment, BookshelfFragment.TAG);
        ft.add(R.id.view_pager, mMagzFragment, MagzFragment.TAG);
        ft.add(R.id.view_pager, mMagzFragment2, MagzFragment2.TAG);
//        ft.hide(mBookshelfFragment);
//        ft.hide(mMagzFragment);
//        ft.hide(mMagzFragment2);
        ft.commit();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);
        return v;
    }
}

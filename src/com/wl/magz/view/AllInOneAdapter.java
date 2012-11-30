package com.wl.magz.view;

import java.util.ArrayList;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;

public class AllInOneAdapter extends PagerAdapter{
    
    private ArrayList<Fragment> mFragments;
    private FragmentActivity mActivity;
    private FragmentManager mFm;
    private FragmentTransaction mFt;
    public AllInOneAdapter(FragmentActivity activity) {
        mActivity = activity;
        mFm = mActivity.getSupportFragmentManager();
        mFt = mFm.beginTransaction();
    }
    
    public void setFragments(ArrayList<Fragment> fragments) {
        mFragments = fragments;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        Object v = mFragments.get(arg1);
        FragmentTransaction ft = mFm.beginTransaction();
        ft.hide((Fragment) v);
    }

    @Override
    public void finishUpdate(View arg0) {
        if (mFt != null) {
            mFt.commitAllowingStateLoss();
            mFt = null;
            mFm.executePendingTransactions();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        Object v = mFragments.get(arg1);
        FragmentTransaction ft = mFm.beginTransaction();
        ft.show((Fragment) v);
        return v;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return ((Fragment) arg1).getView() == arg0;
    }

    @Override
    public void restoreState(Parcelable arg0, ClassLoader arg1) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void startUpdate(View arg0) {
    }
}

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
    private FragmentTransaction mCurTransaction;
    public AllInOneAdapter(FragmentActivity activity) {
        mActivity = activity;
        mFm = mActivity.getSupportFragmentManager();
    }
    
    public void setFragments(ArrayList<Fragment> fragments) {
        mFragments = fragments;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        if (mCurTransaction == null) {
            mCurTransaction = mFm.beginTransaction();
        }
        mCurTransaction.detach((Fragment)arg2);
    }

    @Override
    public void finishUpdate(View arg0) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFm.executePendingTransactions();
        }
    }

    @Override
    public Object instantiateItem(View arg0, int arg1) {
        if (mCurTransaction == null) {
            mCurTransaction = mFm.beginTransaction();
        }
        long id = getItemId(arg1);
        String tag = makeFragmentName(arg0.getId(), id);
        Fragment f = mFm.findFragmentByTag(tag);
        if (f != null) {
            mCurTransaction.attach(f);
        } else {
            f = getItem(arg1);
            mCurTransaction.add(arg0.getId(), f, makeFragmentName(arg0.getId(), id));
        }
        return f;
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
    private Fragment getItem(int position) {
        return mFragments.get(position);
    }
    private long getItemId(int position) {
        return position;
    }
    private String makeFragmentName(long viewId, long id) {
        return "Fragment-viewId:" + viewId + " Fragment id:" + id;
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

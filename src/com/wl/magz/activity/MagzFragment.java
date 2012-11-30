package com.wl.magz.activity;

import com.wl.magz.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MagzFragment extends Fragment{
    public static final String TAG = "MagzFragment";

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup vp, Bundle bundle) {
        View v =  inflater.inflate(R.layout.welcome, vp, false);
        return v;
        
    }

}

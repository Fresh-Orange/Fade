package com.sysu.pro.fade.my.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

/**
 * Created by 12194 on 2018/1/30.
 */

public class MyLiveFragment extends Fragment {

    MyLiveContent myLiveContent;
    View rootView;
    public MyLiveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        myLiveContent = new MyLiveContent(getActivity(),getContext(), rootView);
        return rootView;
    }

}

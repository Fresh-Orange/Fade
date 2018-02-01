package com.sysu.pro.fade.home.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

/**
 * Created by 12194 on 2018/1/30.
 */

public class OtherLiveFragment extends Fragment {

    OtherLiveContent otherLiveContent;
    View rootView;
    int userId;

    public OtherLiveFragment() {
        //空，不会用到
    }

    public static OtherLiveFragment newInstance(int userId) {
        final OtherLiveFragment f = new OtherLiveFragment();
        final Bundle args = new Bundle();
        args.putInt("USER_ID", userId);
        f.setArguments(args);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Integer userId = getArguments() != null ? getArguments().getInt("USER_ID") : null;
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        otherLiveContent = new OtherLiveContent(getActivity(),getContext(), rootView, userId);
        return rootView;
    }

}

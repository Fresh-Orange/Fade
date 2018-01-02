package com.sysu.pro.fade.my.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFadeFragment extends Fragment {
	MyFadeContent myFadeContent;
	View rootView;
	public MyFadeFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_home, container, false);
		myFadeContent = new MyFadeContent(getActivity(),getContext(), rootView);
		return rootView;
	}

}

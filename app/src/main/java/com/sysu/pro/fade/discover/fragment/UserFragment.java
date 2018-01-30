package com.sysu.pro.fade.discover.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserFragment extends Fragment {
	UserContent userContent;
	View rootView;
	public UserFragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.discover_user, container, false);
		userContent = new UserContent(getActivity(),getContext(), rootView);
		return rootView;
	}

}

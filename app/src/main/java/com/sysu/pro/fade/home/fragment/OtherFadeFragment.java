package com.sysu.pro.fade.home.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sysu.pro.fade.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class OtherFadeFragment extends Fragment {
	OtherFadeContent otherFadeContent;
	View rootView;
	int userId;

	public OtherFadeFragment() {
		//空，不会用到
	}

	public static OtherFadeFragment newInstance(int userId) {
		final OtherFadeFragment f = new OtherFadeFragment();
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
		otherFadeContent = new OtherFadeContent(getActivity(),getContext(), rootView, userId);
		return rootView;
	}

}

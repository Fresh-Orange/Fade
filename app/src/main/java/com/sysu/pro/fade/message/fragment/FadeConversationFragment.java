package com.sysu.pro.fade.message.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import io.rong.imkit.fragment.ConversationFragment;

/**
 * Created by LaiXiancheng on 2018/1/26.
 * Email: lxc.sysu@qq.com
 */

public class FadeConversationFragment extends ConversationFragment {


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		init();
	}

	private void init(){
		EditText mEditText = getView().findViewById(io.rong.imkit.R.id.rc_edit_text);
		final FrameLayout mSendToggle = getView().findViewById(io.rong.imkit.R.id.rc_send_toggle);
		final ViewGroup mPluginLayout = getView().findViewById(io.rong.imkit.R.id.rc_plugin_layout);
		ImageView emotion = getView().findViewById(io.rong.imkit.R.id.rc_emoticon_toggle);
		emotion.setVisibility(View.GONE);
		mEditText.addTextChangedListener(new TextWatcher() {
			private int start;
			private int count;

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {


			}

			public void afterTextChanged(Editable s) {
				mSendToggle.setVisibility(View.VISIBLE);
				mPluginLayout.setVisibility(View.VISIBLE);
			}
		});
		mEditText.setOnFocusChangeListener(null);
	}
}

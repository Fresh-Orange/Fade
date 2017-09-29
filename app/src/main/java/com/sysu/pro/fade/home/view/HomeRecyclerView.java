package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by LaiXiancheng on 2017/9/4.
 * Email: lxc.sysu@qq.com
 */

public class HomeRecyclerView extends RecyclerView {
	public HomeRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HomeRecyclerView(Context context) {
		super(context);
	}

	public HomeRecyclerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
}

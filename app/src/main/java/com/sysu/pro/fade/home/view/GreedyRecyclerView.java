package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 事件自己消费，永远不传递给父布局的RecyclerView
 * Created by LaiXiancheng on 2018/1/1.
 * Email: lxc.sysu@qq.com
 */

public class GreedyRecyclerView extends RecyclerView {

	public GreedyRecyclerView(Context context) {
		super(context);
	}

	public GreedyRecyclerView(Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	public GreedyRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 返回true。表示告诉父布局这个事件我自己处理了，就不给你处理了
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return true;
	}
}

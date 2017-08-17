package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.reflect.Field;

/**
 * Created by LaiXiancheng on 2017/7/28.
 * 重写ViewPager，修改其最小滑动速度以及改变滑动拦截条件
 */

public class FixBugViewpager extends ViewPager {

	private Context mContext;
	public FixBugViewpager(Context context) {
		super(context);
		this.mContext = context;
		fixTouchSlop();
	}
	public FixBugViewpager(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		fixTouchSlop();
	}

	/**
	 *通过反射，修改viewpager的触发切换的最小滑动速度，速度超过10dp就给它切换
	 **/
	private void fixTouchSlop() {
		Field field = null;
		try {
			field = ViewPager.class.getDeclaredField("mMinimumVelocity");
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		field.setAccessible(true);
		try {
			field.setInt(this, px2dip(mContext, 10));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/****
	 * 滑动距离及坐标 归还父控件焦点
	 ****/
	private float xDistance, yDistance, xLast, yLast;
	/**
	 * 是否是左右滑动
	 **/
	private boolean mIsBeingDragged = true;

	/**
	 *告诉外部的Recyclerview，滑动小于30度的时候不要拦截viewpager的滑动，让viewpager左右滑
	 **/
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		getParent().requestDisallowInterceptTouchEvent(true);
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				xDistance = yDistance = 0f;
				xLast = ev.getX();
				yLast = ev.getY();
				break;
			case MotionEvent.ACTION_MOVE:
				final float curX = ev.getX();
				final float curY = ev.getY();
				xDistance += Math.abs(curX - xLast);
				yDistance += Math.abs(curY - yLast);
				xLast = curX;
				yLast = curY;
				if (!mIsBeingDragged) {
					if (yDistance < xDistance * 0.5) {//小于30度都左右滑
						mIsBeingDragged = true;
						getParent().requestDisallowInterceptTouchEvent(true);
						getParent().getParent().requestDisallowInterceptTouchEvent(true);
					} else {
						mIsBeingDragged = false;
						getParent().requestDisallowInterceptTouchEvent(false);
						getParent().getParent().requestDisallowInterceptTouchEvent(false);
					}
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				mIsBeingDragged = false;
				break;
		}
		return super.dispatchTouchEvent(ev);
	}
	private int px2dip(Context context, float pxValue) { final float scale = context.getResources().getDisplayMetrics().density; return (int) (pxValue / scale + 0.5f); }



}


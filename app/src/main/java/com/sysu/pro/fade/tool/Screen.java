package com.sysu.pro.fade.tool;

import android.content.Context;

/**
 * Created by LaiXiancheng on 2017/7/30.
 * 与屏幕适配有关的一些操作
 */

public class Screen {
	/**
	 * 单位转化，dp转px
	 * @param dp dp值
	 * @param context 上下文
	 * @return 对应的px值
	 */
	static public int Dp2Px(int dp, Context context){
		final float scale = context.getResources().getDisplayMetrics().density;
		return  (int) (dp * scale + 0.5f);
	}

	/**
	 * 获得屏幕宽度（px）
	 * @param context 上下文
	 * @return 屏幕宽度（px）
	 */
	static public int getScreenWidth(Context context){
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获得屏幕高度（px）
	 * @param context 上下文
	 * @return 屏幕高度（px）
	 */
	static public int getScreenHeight(Context context){
		return context.getResources().getDisplayMetrics().heightPixels;
	}
}

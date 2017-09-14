package com.sysu.pro.fade.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by LaiXiancheng on 2017/9/14.
 * Email: lxc.sysu@qq.com
 */

public class SystemUtils {
	public static void closeKeyboard(Context context) {
		View view = ((Activity) context).getWindow().peekDecorView();
		if (view != null) {
			InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
}

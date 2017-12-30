package com.sysu.pro.fade.publish.crop;

import android.content.Context;

/**
 * Created by LaiXiancheng on 2017/12/28.
 * Email: lxc.sysu@qq.com
 */

public class FixedCropImageView extends CropImageView {

	public FixedCropImageView(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(true, left, top, right, bottom);
	}
}

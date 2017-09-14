package com.sysu.pro.fade.home.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

/**
 * Created by LaiXiancheng on 2017/9/12.
 * Email: lxc.sysu@qq.com
 */

public class imageUtils {
	static public void loadImage(Context context, String url,
								 final ImageView imageView,
								 final int x1,final int y1, final int width, final int height) {
		Glide.with(context)
				.load(url)
				.asBitmap()
				.into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
					@Override
					public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
						int h = resource.getHeight();
						int w = resource.getWidth();
						int min = h < w ? h : w;
						Bitmap cropeedBitmap = Bitmap.createBitmap(resource, x1, y1, min, min);
						imageView.setImageBitmap(cropeedBitmap);
					}
				});
	}
}

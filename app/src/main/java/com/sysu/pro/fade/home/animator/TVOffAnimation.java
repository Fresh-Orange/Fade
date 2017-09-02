package com.sysu.pro.fade.home.animator;

/**
 * Created by LaiXiancheng on 2017/8/4.
 * item的消失动画效果，模拟电视关闭效果
 */
import android.graphics.Matrix;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class TVOffAnimation extends Animation {
	int halfWidth;
	int halfHeight;

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		//设置动画时间为700毫秒
		setDuration(getDuration());
		//设置动画结束后就结束在动画结束的时刻
		setFillAfter(true);
		//保存View的中心点
		halfWidth=width/2;
		halfHeight=height/2;
		//设置动画先加速后减速
		setInterpolator(new AccelerateDecelerateInterpolator());
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {


		final Matrix matrix=t.getMatrix();
		//interpolatedTime是从0~1的一个变化，前85%让动画缩小成一个线，后15%保持线的高度缩小线的宽度
		if (interpolatedTime<0.85){
			matrix.preScale(1,1-interpolatedTime/0.85f+0.01f,halfWidth,halfHeight);
			t.setAlpha(1-interpolatedTime/0.85f);
		}else{
			matrix.setScale(6.5f*(1-interpolatedTime),0.01f,halfWidth,halfHeight);
		}

	}


}

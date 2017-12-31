package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sysu.pro.fade.publish.utils.DensityUtils;

/**
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class TextProgressBar extends ProgressBar {
	private String str = "test";
	private Paint mPaint;
	Rect rect;

	public TextProgressBar(Context context) {
		super(context);
		init();
	}

	public TextProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public TextProgressBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}


	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		this.mPaint.getTextBounds(this.str, 0, this.str.length(), rect);
		int x = (int)(getWidth()*((getProgress()-2)*1.0/100) - rect.right);//减2是为了文字左移一点
		int y = getHeight() + rect.centerY();
		canvas.drawText(this.str, x, y, this.mPaint);
	}

	// 初始化，画笔
	private void init() {
		rect = new Rect();
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setTextSize(DensityUtils.sp2px(getContext(), 12));
		this.mPaint.setColor(Color.argb(122, 0,0,0));
	}

	// 设置文字内容
	public void setTimeText(String text) {
		this.str = text;
		invalidate();
	}

}

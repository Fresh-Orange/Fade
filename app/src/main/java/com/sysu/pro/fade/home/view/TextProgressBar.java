package com.sysu.pro.fade.home.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sysu.pro.fade.publish.utils.DensityUtils;
import com.sysu.pro.fade.utils.Screen;

/**
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class TextProgressBar extends ProgressBar {
	private String str = "test";
	private Paint mPaint;
	private int color = Color.argb(122, 0,0,0);

	Rect rect;

	public void setColor(int color) {
		this.color = color;
		init();
		invalidate();
	}

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
		int x = (int)(getWidth()*((getProgress()-20)*1.0/1000) - rect.right);//减2是为了文字左移一点
		int y = getHeight() / 2 + rect.height()/2 - Screen.Dp2Px(1,getContext());/// + rect.centerY();
		canvas.drawText(this.str, x, y, this.mPaint);
	}

	// 初始化，画笔
	private void init() {
		rect = new Rect();
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setFakeBoldText(true);
		this.mPaint.setTextSize(DensityUtils.sp2px(getContext(), 12));
		this.mPaint.setColor(color);
		this.setMax(1000);
	}

	// 设置文字内容
	public void setTimeText(String text) {
		this.str = text;
		invalidate();
	}

}

package com.sysu.pro.fade.home.listener;

import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/**
 * Created by LaiXiancheng on 2017/9/4.
 * Email: lxc.sysu@qq.com
 * 解决Android TextView富文本拦截点击事件
 * from: http://www.jianshu.com/p/413184996fc8
 */

public class RelayClickMovementMethod implements View.OnTouchListener {
	private static RelayClickMovementMethod sInstance;

	public static RelayClickMovementMethod getInstance() {
		if (sInstance == null) {
			sInstance = new RelayClickMovementMethod();
		}
		return sInstance;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		boolean ret = false;
		TextView widget = (TextView) v;
		CharSequence text = widget.getText();
		Spannable spannable = Spannable.Factory.getInstance().newSpannable(text);
		int action = event.getAction();
		if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			x -= widget.getTotalPaddingLeft();
			y -= widget.getTotalPaddingTop();
			x += widget.getScrollX();
			y += widget.getScrollY();
			Layout layout = widget.getLayout();
			int line = layout.getLineForVertical(y);
			int off = layout.getOffsetForHorizontal(line, x);
			ClickableSpan[] link = spannable.getSpans(off, off, ClickableSpan.class);
			if (link.length != 0) {
				if (action == MotionEvent.ACTION_UP) {
					link[0].onClick(widget);
				}
				ret = true;
			}
		}
		return ret;
	}
}

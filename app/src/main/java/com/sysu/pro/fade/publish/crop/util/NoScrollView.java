package com.sysu.pro.fade.publish.crop.util;

/**
 * Created by yellow on 2017/12/26.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class NoScrollView extends ScrollView {
    public NoScrollView(Context context) {
        super(context);
    }

    public NoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) { // 屏蔽touch事件,才能在监听其子控件的touch事件
        // TODO Auto-generated method stub
        super.onTouchEvent(ev);
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)// 屏蔽touch事件传递,才能在监听其子控件的touch事件
    {
        super.onInterceptTouchEvent(event);
        return false;
    }
}

package com.sysu.pro.fade.publish.crop.util;

/**
 * Created by yellow on 2017/12/26.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;
import android.widget.Scroller;

public class NoScrollView extends ScrollView {
    private Scroller mScroller;
    public NoScrollView(Context context) {
        super(context);
        mScroller = new Scroller(context);
    }

    public NoScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
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

    //调用此方法设置滚动的相对偏移
    public void smoothScrollBySlow(int dx, int dy) {
        //设置mScroller的滚动偏移量
        mScroller.startScroll(getScrollX(), getScrollY(), dx, dy, 200);//scrollView使用的方法（因为可以触摸拖动）
        invalidate();//这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
    }

    public final void smoothScrollToSlow(int x, int y) {
        smoothScrollBySlow(x - getScrollX(), y - getScrollY());
    }

    @Override
    public void computeScroll() {

        //先判断mScroller滚动是否完成
        if (mScroller.computeScrollOffset()) {

            //这里调用View的scrollTo()完成实际的滚动
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());

            //必须调用该方法，否则不一定能看到滚动效果
            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 滑动事件，这是控制手指滑动的惯性速度
     */
    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 4);
    }
}

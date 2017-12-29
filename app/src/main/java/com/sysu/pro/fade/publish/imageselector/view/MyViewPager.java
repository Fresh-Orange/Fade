package com.sysu.pro.fade.publish.imageselector.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * 继承ViewPager并在onInterceptTouchEvent捕捉异常。
 * 因为ViewPager嵌套PhotoView使用，有时候会发生IllegalArgumentException异常。
 */
public class MyViewPager extends ViewPager {

    private OnItemClickListener mOnItemClickListener;
    private GestureDetector tapGestureDetector;
    public MyViewPager(Context context) {
        super(context);
        setup();
    }


    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            super.onInterceptTouchEvent(ev);
            Log.d("Yellow", "touch");
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void setup() {
        tapGestureDetector = new GestureDetector(getContext(), new TapGestureListener());
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("Yellow", "onTouch");
                tapGestureDetector.onTouchEvent(event);
                return false;
            }
        });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private class TapGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.d("Yellow", "null");
            if(mOnItemClickListener != null) {
                Log.d("Yellow", "notNull");
                mOnItemClickListener.onItemClick(getCurrentItem());
            }
            return false;
        }
    }


}

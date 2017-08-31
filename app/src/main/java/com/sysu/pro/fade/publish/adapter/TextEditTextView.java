package com.sysu.pro.fade.publish.adapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.Toast;

import static com.sysu.pro.fade.publish.PublishActivity.publishActivity;


/**
 * Created by yellow on 2017/7/29.
 */

public class TextEditTextView extends android.support.v7.widget.AppCompatEditText {
    public TextEditTextView(Context context) {
        super(context);
    }

    public TextEditTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextEditTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == 1) {
            super.onKeyPreIme(keyCode, event);
            Toast.makeText(publishActivity, "隐藏", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
}

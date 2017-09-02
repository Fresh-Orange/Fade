package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sysu.pro.fade.R;

/**
 * Created by 12194 on 2017/8/4.
 * 自定义控件，可以直接使用
 * 设置界面的标题栏，只有一个返回的图片，设置其监听器，点击返回上一个活动
 */

public class BackBar extends LinearLayout {
    public BackBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.backbar, this);
        ImageView back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) getContext()).finish();
            }
        });
    }

}

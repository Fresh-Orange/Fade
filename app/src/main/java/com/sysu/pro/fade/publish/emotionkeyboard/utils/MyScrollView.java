package com.sysu.pro.fade.publish.emotionkeyboard.utils;

import android.content.Context;
import android.widget.ScrollView;

/**
 * Created by yellow on 2017/8/11.
 */

public class MyScrollView extends ScrollView {
    private boolean need = true;
    public MyScrollView(Context context) {
        super(context);
    }

    public void changeNeed() {
        need = false;
    }
    public void computeScroll() {
        if (need)
            super.computeScroll();
        else
            need = true;
    }
}

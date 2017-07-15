package com.sysu.pro.fade.my;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by road on 2017/7/14.
 */
public class ContentMy {
    private Activity activity;
    private Context context;
    private View rootview;

    public ContentMy(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        loadData();
    }

    public static void loadData(){

    }


}

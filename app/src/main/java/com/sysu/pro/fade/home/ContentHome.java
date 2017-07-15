package com.sysu.pro.fade.home;

import android.app.Activity;
import android.content.Context;
import android.view.View;

/**
 * Created by road on 2017/7/14.
 */
public class ContentHome {
    private Activity activity;
    private Context context;
    private View rootview;

    public ContentHome(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        loadData();
    }

    public static void loadData(){

    }
}

package com.sysu.pro.fade.discover;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by road on 2017/7/14.
 */
public class ContentDiscover {
    private Activity activity;
    private Context context;
    private View rootview;

    public ContentDiscover(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        //初始化数据
        loadData();
    }

    public static void loadData(){

    }
}

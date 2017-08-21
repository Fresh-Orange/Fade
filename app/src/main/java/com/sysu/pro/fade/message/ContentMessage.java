package com.sysu.pro.fade.message;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Toast;

/**
 * Created by road on 2017/7/14.
 */
public class ContentMessage {
    private Activity activity;
    private Context context;
    private View rootview;

    public ContentMessage(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;
        Toast.makeText(context,"消息",Toast.LENGTH_SHORT).show();
        loadData();
    }
    public static void loadData(){

    }
}

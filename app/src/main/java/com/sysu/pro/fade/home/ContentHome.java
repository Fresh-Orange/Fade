package com.sysu.pro.fade.home;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.sysu.pro.fade.tool.NoteTool;

/**
 * Created by road on 2017/7/14.
 */
public class ContentHome {
    private Activity activity;
    private Context context;
    private View rootview;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public ContentHome(Activity activity, Context context, View rootview){
        this.activity = activity;
        this.context = context;
        this.rootview = rootview;

        //测试
//        NoteTool.addNote(handler,"8","黄路","https://sysufade.cn/Fade/image/head/8af431_1500709261912.png",
//                "31号安卓端发帖测试","0","标签1,标签2,标签3");
        //NoteTool.getSectionDiscoverRecommond(handler,"0");
//        NoteTool.getTwentyGood(handler,"332","0");

    }

    public void loadData(){

    }
}

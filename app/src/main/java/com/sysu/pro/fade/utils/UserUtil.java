package com.sysu.pro.fade.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.sysu.pro.fade.Const;
import com.sysu.pro.fade.beans.User;

/**
 * Created by road on 2017/8/19.
 */
public class UserUtil {
    private Activity activity;
    public UserUtil(Activity activity){
        this.activity = activity;
    }

    public User getUer(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Const.USER_SHARE, Context.MODE_PRIVATE);
        User user = JSON.parseObject(sharedPreferences.getString("user","{}"),User.class);
        return user;
    }

}

package com.sysu.pro.fade.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.sysu.pro.fade.beans.User;

import static com.sysu.pro.fade.utils.Const.USER_SHARE;

/**
 * Created by road on 2017/8/19.
 */
public class UserUtil {
    private static  User  user= new User();
    private Activity activity;
    public UserUtil(Activity activity){
        this.activity = activity;
    }

    public User getUer(){
        SharedPreferences sharedPreferences = activity.getSharedPreferences(USER_SHARE, Context.MODE_PRIVATE);
        user.setUser_id(sharedPreferences.getInt(Const.USER_ID,0));
        user.setNickname(sharedPreferences.getString(Const.NICKNAME,""));
        user.setAera(sharedPreferences.getString(Const.AREA,""));
        user.setConcern_num(sharedPreferences.getInt(Const.CONCERN_NUM,0));
        user.setFans_num(sharedPreferences.getInt(Const.FANS_NUM,0));
        user.setFade_name(sharedPreferences.getString(Const.FADE_NAME,""));
        user.setHead_image_url(sharedPreferences.getString(Const.HEAD_IMAGE_URL,""));
        user.setMail(sharedPreferences.getString(Const.MAIL,""));
        user.setPassword(sharedPreferences.getString(Const.PASSWORD,""));
        user.setRegister_time(sharedPreferences.getString(Const.REGISTER_TIME,""));
        user.setWechat_id(sharedPreferences.getString(Const.WECHAT_ID,""));
        user.setWeibo_id(sharedPreferences.getString(Const.WEIBO_ID,""));
        user.setQq_id(sharedPreferences.getString(Const.QQ_ID,""));
        user.setSex(sharedPreferences.getString(Const.SEX,"男"));
        user.setWallpapaer_url(sharedPreferences.getString(Const.WALLPAPER_URL,""));
        user.setTelephone(sharedPreferences.getString(Const.TELEPHONE,""));
        user.setSummary(sharedPreferences.getString(Const.SUMMARY,""));
        return user;
    }

}

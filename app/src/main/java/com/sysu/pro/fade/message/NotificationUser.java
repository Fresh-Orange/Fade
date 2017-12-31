package com.sysu.pro.fade.message;

import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.sysu.pro.fade.publish.imageselector.entry.Image;



/**
 * Created by yellow on 2017/12/30.
 */

public class NotificationUser
{
    private Uri user_icon;    //头像
    private int user_count;    //聊天数
    private String user_id;       //用户名字
    private String user_content;  //聊天内容
    private String user_time;     //聊天时间

    NotificationUser(Uri user_icon, int user_count, String user_id, String user_content, String user_time) {
        this.user_icon = user_icon;
        this.user_count = user_count;
        this.user_id = user_id;
        this.user_content = user_content;
        this.user_time = user_time;
    }
    public void setUser_content(String user_content) {
        this.user_content = user_content;
    }

    public void setUser_count(int user_count) {
        this.user_count = user_count;
    }

    public void setUser_icon(Uri user_icon) {
        this.user_icon = user_icon;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setUser_time(String user_time) {
        this.user_time = user_time;
    }

    public Uri getUser_icon() {
        return user_icon;
    }

    public String getUser_content() {
        return user_content;
    }

    public int getUser_count() {
        return user_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_time() {
        return user_time;
    }
}

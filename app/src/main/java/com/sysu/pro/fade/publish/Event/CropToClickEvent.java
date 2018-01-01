package com.sysu.pro.fade.publish.Event;

/**
 * Created by LaiXiancheng on 2018/1/1.
 * Email: lxc.sysu@qq.com
 */

public class CropToClickEvent {
    String message;
    int position;
    public CropToClickEvent(String message, int position) {this.message = message;
    this.position = position;}

    public String getMessage() {
        return message;
    }

    public int getPosition() {
        return position;
    }
}

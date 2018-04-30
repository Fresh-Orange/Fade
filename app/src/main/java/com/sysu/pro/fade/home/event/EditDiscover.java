package com.sysu.pro.fade.home.event;

/**
 * Created by yellow on 2018/4/30.
 */

public class EditDiscover {
    String message;
    boolean hasFocus;

    public EditDiscover(String message, boolean hasFocus) {
        this.message = message;
        this.hasFocus = hasFocus;
    }

    public boolean isHasFocus() {
        return hasFocus;
    }

    public String getMessage() {
        return message;
    }
}

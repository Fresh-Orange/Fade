package com.sysu.pro.fade.home.event;

import com.sysu.pro.fade.beans.User;

/**
 * Created by yellow on 2018/5/1.
 */

public class RefreshNum {
    String message;
    User user;

    public RefreshNum(String message, User user) {
        this.message = message;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }
}

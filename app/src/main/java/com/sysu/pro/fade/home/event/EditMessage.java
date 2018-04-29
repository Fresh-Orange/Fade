package com.sysu.pro.fade.home.event;

/**
 * Created by yellow on 2018/4/29.
 */

public class EditMessage {
    String message;
    boolean click;

    public EditMessage(String message, boolean click) {
        this.message = message;
        this.click = click;
    }

    public boolean isClick() {
        return click;
    }

    public String getMessage() {
        return message;
    }
}

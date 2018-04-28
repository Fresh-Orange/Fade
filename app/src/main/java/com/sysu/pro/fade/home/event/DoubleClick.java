package com.sysu.pro.fade.home.event;

/**
 * Created by yellow on 2018/4/26.
 */

public class DoubleClick {
    String message;
    boolean click;

    public DoubleClick(String message, boolean click) {
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

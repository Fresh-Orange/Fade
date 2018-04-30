package com.sysu.pro.fade.my.Event;

/**
 * Created by yellow on 2018/4/26.
 */

public class DoubleConcern {
    String message;
    boolean click;

    public DoubleConcern(String message, boolean click) {
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

package com.sysu.pro.fade.message.Event;

/**
 * Created by yellow on 2018/4/30.
 */

public class ContributeEvent {
    String message;
    boolean click;

    public ContributeEvent(String message, boolean click) {
        this.message = message;
        this.click = click;
    }

    public boolean isBack() {
        return click;
    }

    public String getMessage() {
        return message;
    }
}

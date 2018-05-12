package com.sysu.pro.fade.my.Event;

/**
 * Created by 12194 on 2018/5/9.
 */

public class NewNumber {

    private Integer liveNum;
    private Integer fadeNum;
    private Integer fansNum;
    private Integer concernNum;

    public NewNumber(Integer liveNum, Integer fadeNum, Integer fansNum, Integer concernNum) {
        this.liveNum = liveNum;
        this.fadeNum = fadeNum;
        this.fansNum = fansNum;
        this.concernNum = concernNum;
    }

    public Integer getLiveNum() {
        return liveNum;
    }

    public Integer getFadeNum() {
        return fadeNum;
    }

    public Integer getFansNum() {
        return fansNum;
    }

    public Integer getConcernNum() {
        return concernNum;
    }
}

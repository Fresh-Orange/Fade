package com.sysu.pro.fade.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by road on 2017/9/4.
 */
public class DetailGood implements Serializable{
    private String head_image_url;
    private String summary;
    private String good_time;
    private String nickname;
    private Integer user_id;
    private Integer good_id;

    public String getGood_time() {
        return good_time;
    }

    public void setGood_time(String good_time) {
        this.good_time = good_time;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHead_image_url() {
        return head_image_url;
    }

    public void setHead_image_url(String head_image_url) {
        this.head_image_url = head_image_url;
    }


    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getGood_id() {
        return good_id;
    }

    public void setGood_id(Integer good_id) {
        this.good_id = good_id;
    }
}

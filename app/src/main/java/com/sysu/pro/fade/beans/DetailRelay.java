package com.sysu.pro.fade.beans;

import java.io.Serializable;

/**
 * Created by road on 2017/9/4.
 */
public class DetailRelay implements Serializable {
    private String head_image_url;
    private Integer user_id;
    private String post_time; //转发时间
    private String nickname;

    public String getHead_image_url() {
        return head_image_url;
    }

    public void setHead_image_url(String head_image_url) {
        this.head_image_url = head_image_url;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getPost_time() {
        return post_time;
    }

    public void setPost_time(String post_time) {
        this.post_time = post_time;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}

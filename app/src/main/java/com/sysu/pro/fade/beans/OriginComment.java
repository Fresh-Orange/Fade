package com.sysu.pro.fade.beans;

/**
 * Created by road on 2017/8/31.
 */
public class OriginComment {
    /**
     * 原评论内容
     */
    //原评论的数据会少一些
    private Integer user_id;
    private String nickname;
    private String comment_content;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getComment_content() {
        return comment_content;
    }

    public void setComment_content(String comment_content) {
        this.comment_content = comment_content;
    }
}

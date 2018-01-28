package com.sysu.pro.fade.beans;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by road on 2018/1/28.
 */

public class CommentMessageQuery {
    private List<CommentMessage>list = new ArrayList<>();
    private Integer start;
    private String point;
    public List<CommentMessage> getList() {
        return list;
    }

    public void setList(List<CommentMessage> list) {
        this.list = list;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }
}

package com.sysu.pro.fade.home.beans;

import java.io.Serializable;
import java.util.List;

/**
 * 信息流中一个item的全部所需内容
 */
public class ContentBean implements Serializable {

    private int id; //id
    private String name;    //姓名
    private String text;   //标题
    private List<String> imgUrls;  //图片数组
    private List<RelayBean> relayBeans; //转发的信息列表

    public ContentBean(int id, String name, String text, List<String> imgUrls, List<RelayBean> relayBeans) {
        this.id = id;
        this.name = name;
        this.text = text;
        this.imgUrls = imgUrls;
        this.relayBeans = relayBeans;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(List<String> imgUrls) {
        this.imgUrls = imgUrls;
    }

    public List<RelayBean> getRelayBeans() {
        return relayBeans;
    }

    public void setRelayBeans(List<RelayBean> relayBeans) {
        this.relayBeans = relayBeans;
    }

    @Override
    public String toString() {
        return "ContentBean{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", imgUrls=" + imgUrls +
                '}';
    }
}

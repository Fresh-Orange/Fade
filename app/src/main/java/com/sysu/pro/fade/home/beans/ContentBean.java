package com.sysu.pro.fade.home.beans;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 信息流中一个item的全部所需内容
 */
public class ContentBean implements Serializable {

    private int user_id;                   //用户id，即是user_id
    private int note_id;                   //帖子id
    private String name;                   //用户名
    private String head_image_url;       //用户头像
    private String text;                  //帖子内容
    private Date post_time;              //发布时间
    private int isRelay;                 //如果是0，则说明是原创的，如果不是0，则代表原贴的note_id

    private List<RelayBean> relayBeans; //转发的信息列表

    private int comment_num;            //评论数量
    private int relay_num;              //转发数量
    private int good_num;               //续一秒数量

    private List<String> imgUrls;        //图片url数组
    private List<Double> imgSizes;       //图片尺寸数组

    private List<String> tag_list;       //标签数组

	private long fetchTime;				//从服务器拿到该node的时间点，这个不需要从服务器拿

    private boolean isGood;

    public boolean isGood() {
        return isGood;
    }

    public void setGood(boolean good) {
        isGood = good;
    }

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}

    public List<String> getTag_list() {
        return tag_list;
    }

    public void setTag_list(List<String> tag_list) {
        this.tag_list = tag_list;
    }

    public ContentBean(){
        /*
        默认构造方法
         */
        comment_num = 0;
        good_num = 0;
        relay_num = 0;
        isRelay = 0;
    }

    public ContentBean(int note_id, String name, String text, List<String> imgUrls, List<RelayBean> relayBeans) {
        this.note_id = note_id;
        this.name = name;
        this.text = text;
        this.imgUrls = imgUrls;
        this.relayBeans = relayBeans;
    }

    public String getHead_image_url() {
        return head_image_url;
    }

    public void setHead_image_url(String head_image_url) {
        this.head_image_url = head_image_url;
    }

    public int getNote_id() {
        return note_id;
    }

    public void setNote_id(int note_id) {
        this.note_id = note_id;
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getRelay_num() {
        return relay_num;
    }

    public void setRelay_num(int relay_num) {
        this.relay_num = relay_num;
    }

    public List<Double> getImgSizes() {
        return imgSizes;
    }

    public void setImgSizes(List<Double> imgSizes) {
        this.imgSizes = imgSizes;
    }

    public int getGood_num() {
        return good_num;
    }

    public void setGood_num(int good_num) {
        this.good_num = good_num;
    }

    public int getComment_num() {
        return comment_num;
    }

    public void setComment_num(int comment_num) {
        this.comment_num = comment_num;
    }

    public int getIsRelay() {
        return isRelay;
    }

    public void setIsRelay(int isRelay) {
        this.isRelay = isRelay;
    }

    public Date getPost_time() {
        return post_time;
    }

    public void setPost_time(Date post_time) {
        this.post_time = post_time;
    }

    @Override
    public String toString() {
        return "ContentBean{" +
                "note_id=" + note_id +
                "user_id=" + user_id +
                "post_time" + post_time +
                "good_num="+ good_num +
                "comment_num=" + comment_num +
                "relay_num=" + relay_num +
                "head_image_url" + head_image_url +
                ", name='" + name + '\'' +
                ", text='" + text + '\'' +
                ", imgUrls=" + imgUrls +
                '}';
    }
}

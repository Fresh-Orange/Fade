package com.sysu.pro.fade.beans;

import java.util.List;

/**
 * Created by LaiXiancheng on 2017/7/30.
 * 转发内容的最小单位
 */

public class RelayNote {
	/**
	 * 转发的文字内容
	 */
	String name;         //用户名
	int user_id;        //用户id
	String content;     //帖子内容

	private List<String> imgUrls;        //图片url数组
	private List<Double> imgSizes;       //图片尺寸数组

	public RelayNote(){
		/**
		 * 默认构造方法
		 */
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public RelayNote(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public List<Double> getImgSizes() {
		return imgSizes;
	}

	public void setImgSizes(List<Double> imgSizes) {
		this.imgSizes = imgSizes;
	}
}

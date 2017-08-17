package com.sysu.pro.fade.home.beans;

/**
 * Created by LaiXiancheng on 2017/7/30.
 * 转发内容的最小单位
 */

public class RelayBean {
	String name;
	String content;

	public RelayBean(String name, String content) {
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
}

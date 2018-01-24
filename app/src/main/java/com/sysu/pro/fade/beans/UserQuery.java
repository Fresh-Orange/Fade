package com.sysu.pro.fade.beans;

import java.util.List;

public class UserQuery {
	private List<User>list;
	private Integer start;

	private String point;//通知页面分段查询用到的,记录时间点
	
	public List<User> getList() {
		return list;
	}
	public void setList(List<User> list) {
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

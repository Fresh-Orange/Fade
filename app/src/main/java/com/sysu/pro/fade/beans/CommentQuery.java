package com.sysu.pro.fade.beans;

import java.util.List;

public class CommentQuery {
	private Integer start;    //发给服务器的，用来查询数据库
	private List<Comment>list;//得到的数据，最多十条

	private String point;//通知页面分段查询用到的,记录时间点

	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public List<Comment> getList() {
		return list;
	}
	public void setList(List<Comment> list) {
		this.list = list;
	}

	public String getPoint() {
		return point;
	}

	public void setPoint(String point) {
		this.point = point;
	}
}

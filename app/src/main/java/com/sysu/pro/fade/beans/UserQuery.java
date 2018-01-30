package com.sysu.pro.fade.beans;

import java.util.List;

public class UserQuery {
	private List<User>list;
	private Integer start;

	private String point;//通知页面分段查询用到的,记录时间点

	private Integer sum;//搜索页用（返回的搜索结果总数量，上限50条）

	public Integer getSum() {
		return sum;
	}

	public void setSum(Integer sum) {
		this.sum = sum;
	}

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

package com.sysu.pro.fade.beans;

import java.util.List;

public class UserQuery {
	private List<User>list; //得到的数据，最多十条
	private Integer start;  //发给服务器的，用来查询数据库
	
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
	
	
}

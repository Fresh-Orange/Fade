package com.sysu.pro.fade.beans;

import java.util.List;

public class PersonPage {
	//个人界面所需要数据
	private User user;
	private Integer isConcern; //0为没关注，1为已关注
	private List<Note>list;//fade列表，10条一次
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public Integer getIsConcern() {
		return isConcern;
	}
	public void setIsConcern(Integer isConcern) {
		this.isConcern = isConcern;
	}

	public List<Note> getList() {
		return list;
	}

	public void setList(List<Note> list) {
		this.list = list;
	}
}

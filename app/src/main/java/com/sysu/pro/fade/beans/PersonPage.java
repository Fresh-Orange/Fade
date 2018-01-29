package com.sysu.pro.fade.beans;

public class PersonPage {
	//请求自己和他人的个人页,如果是自己的个人页，则user_id和my_id都填自己的
	private User user;
	private Integer isConcern; //0为没关注，1为已关注
	private NoteQuery query;//活着的帖子，首次加载最多十条(即动态)
	
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

	public NoteQuery getQuery() {
		return query;
	}

	public void setQuery(NoteQuery query) {
		this.query = query;
	}
}

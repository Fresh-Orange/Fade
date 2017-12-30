package com.sysu.pro.fade.beans;

import java.util.List;

public class DetailPage {
	//帖子详情页
	private List<Note>second_list; //10条增减秒列表
	private List<Comment>comment_list; //10条评论列表
	public List<Note> getSecond_list() {
		return second_list;
	}
	public void setSecond_list(List<Note> second_list) {
		this.second_list = second_list;
	}
	public List<Comment> getComment_list() {
		return comment_list;
	}
	public void setComment_list(List<Comment> comment_list) {
		this.comment_list = comment_list;
	}

}

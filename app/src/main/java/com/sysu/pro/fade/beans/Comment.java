package com.sysu.pro.fade.beans;

import java.io.Serializable;
import java.util.List;

public class Comment implements Serializable{
	//一级评论

	private static final long serialVersionUID = -2152733214028496361L;
	private Integer comment_id;           
	private Integer user_id;             
	private String  nickname;            
	private String  head_image_url;      	 
	private Integer note_id;          
	private String comment_time;      
	private String comment_content;
	private List<SecondComment>comments;//二级评论列表
	private Integer type; //0为没动作，1为对这个帖子增过，2为对这个帖子减过

	private Integer viewType;

	public Integer getComment_id() {
		return comment_id;
	}
	public void setComment_id(Integer comment_id) {
		this.comment_id = comment_id;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getHead_image_url() {
		return head_image_url;
	}
	public void setHead_image_url(String head_image_url) {
		this.head_image_url = head_image_url;
	}
	public Integer getNote_id() {
		return note_id;
	}
	public void setNote_id(Integer note_id) {
		this.note_id = note_id;
	}
	public String getComment_time() {
		return comment_time;
	}
	public void setComment_time(String comment_time) {
		this.comment_time = comment_time;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public List<SecondComment> getComments() {
		return comments;
	}
	public void setComments(List<SecondComment> comments) {
		this.comments = comments;
	}
	public Integer getType() {
		return type;
	}
	public void setType(Integer type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "comment_id="+comment_id + ",comment_content="+comment_content;
	}

	public Integer getViewType() {
		return viewType;
	}

	public void setViewType(Integer viewType) {
		this.viewType = viewType;
	}
}

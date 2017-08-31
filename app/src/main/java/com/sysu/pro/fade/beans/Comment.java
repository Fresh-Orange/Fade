package com.sysu.pro.fade.beans;

import java.io.Serializable;
import java.util.Date;

public class Comment implements Serializable{

	/**
	 * 评论内容
	 */
	private static final long serialVersionUID = -2152733214028496361L;
	private Integer comment_id;            //评论的id
	private Integer user_id;                //评论者的id
	private String  nickname;              //评论者的昵称
	private String  head_image_url;       //评论者的头像
	private Integer to_comment_id;        //如果是0的话，则是对帖子的评论；如果不是0的话，则代表是对某个评论的回复,表示为原来那条评论的id
	 
	private Integer note_id;               //评论的帖子的id
	private Date comment_time;          //评论时间
	private String comment_content;       //评论内容
	private Integer comment_good_num;     //评论点赞数

	private OriginComment originComment;  //如果是对某条评论的回复的话，则不为null

	public OriginComment getOriginComment() {
		return originComment;
	}

	public void setOriginComment(OriginComment originComment) {
		this.originComment = originComment;
	}

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
	public Integer getTo_comment_id() {
		return to_comment_id;
	}
	public void setTo_comment_id(Integer to_comment_id) {
		this.to_comment_id = to_comment_id;
	}
	public String getComment_content() {
		return comment_content;
	}
	public void setComment_content(String comment_content) {
		this.comment_content = comment_content;
	}
	public Integer getComment_good_num() {
		return comment_good_num;
	}
	public void setComment_good_num(Integer comment_good_num) {
		this.comment_good_num = comment_good_num;
	}

	public Date getComment_time() {
		return comment_time;
	}

	public void setComment_time(Date comment_time) {
		this.comment_time = comment_time;
	}
}

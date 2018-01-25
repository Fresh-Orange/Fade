package com.sysu.pro.fade.beans;

public class DetailPage {
	//帖子详情页
	private NoteQuery noteQuery; //包含10条增减秒列表，以及下一次分页查询的start
	private CommentQuery commentQuery; //10条评论列表，以及下一次分页查询的start
	private Integer comment_num; //这三个数量用于更新
	private Integer add_num;
	private Integer sub_num;
	private Long fetchTime; 
	
	public Integer getComment_num() {
		return comment_num;
	}
	public void setComment_num(Integer comment_num) {
		this.comment_num = comment_num;
	}
	public Integer getAdd_num() {
		return add_num;
	}
	public void setAdd_num(Integer add_num) {
		this.add_num = add_num;
	}
	public Integer getSub_num() {
		return sub_num;
	}
	public void setSub_num(Integer sub_num) {
		this.sub_num = sub_num;
	}
	
	public Long getFetchTime() {
		return fetchTime;
	}
	public void setFetchTime(Long fetchTime) {
		this.fetchTime = fetchTime;
	}
	public NoteQuery getNoteQuery() {
		return noteQuery;
	}
	public void setNoteQuery(NoteQuery noteQuery) {
		this.noteQuery = noteQuery;
	}
	public CommentQuery getCommentQuery() {
		return commentQuery;
	}
	public void setCommentQuery(CommentQuery commentQuery) {
		this.commentQuery = commentQuery;
	}
	
	
}

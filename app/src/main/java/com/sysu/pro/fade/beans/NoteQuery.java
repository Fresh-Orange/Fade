package com.sysu.pro.fade.beans;

import java.util.List;
import java.util.Set;

public class NoteQuery {
	private Integer start; //向下加载查询的起点，一开始填0
	private List<Note>list;//查询得到的新数据
	private Set<Note>updateSet;//已加载的帖子集合 再 经过服务器查询筛选出来的剩余贴子
	
	public Integer getStart() {
		return start;
	}
	public void setStart(Integer start) {
		this.start = start;
	}
	public List<Note> getList() {
		return list;
	}
	public void setList(List<Note> list) {
		this.list = list;
	}
	public Set<Note> getUpdateSet() {
		return updateSet;
	}
	public void setUpdateSet(Set<Note> updateSet) {
		this.updateSet = updateSet;
	}

	
	
	
	
}

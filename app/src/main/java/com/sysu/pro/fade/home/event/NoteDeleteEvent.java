package com.sysu.pro.fade.home.event;

/**
 * 信息流item删除的事件
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class NoteDeleteEvent {
	int noteId;

	public NoteDeleteEvent(int noteId) {
		this.noteId = noteId;
	}

	public int getNoteId() {
		return noteId;
	}

	public void setNoteId(int noteId) {
		this.noteId = noteId;
	}
}

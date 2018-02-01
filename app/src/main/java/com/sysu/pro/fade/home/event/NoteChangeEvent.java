package com.sysu.pro.fade.home.event;

import com.sysu.pro.fade.beans.Note;

/**
 * 信息流item界面变动的事件
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class NoteChangeEvent {
	int originalNoteId;
	Note note;

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public NoteChangeEvent(int originalNoteId, Note note) {
		this.originalNoteId = originalNoteId;
		this.note = note;
	}

	public int getOriginalNoteId() {
		return originalNoteId;
	}

	public void setOriginalNoteId(int originalNoteId) {
		this.originalNoteId = originalNoteId;
	}
}

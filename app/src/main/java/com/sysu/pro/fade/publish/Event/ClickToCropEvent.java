package com.sysu.pro.fade.publish.Event;

/**
 * Created by LaiXiancheng on 2018/1/1.
 * Email: lxc.sysu@qq.com
 */

public class ClickToCropEvent {
	int curPosition;

	public ClickToCropEvent(int curPosition) {
		this.curPosition = curPosition;
	}

	public int getCurPosition() {
		return curPosition;
	}

	public void setCurPosition(int curPosition) {
		this.curPosition = curPosition;
	}
}

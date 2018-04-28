package com.sysu.pro.fade.home.event;

/**
 * Created by LaiXiancheng on 2018/4/27.
 * Email: lxc.sysu@qq.com
 */

public class NoteConcernChangeEvent {
	Integer userId;
	boolean isConcerned;

	public NoteConcernChangeEvent(int userId, boolean isConcerned) {
		this.userId = userId;
		this.isConcerned = isConcerned;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public boolean isConcerned() {
		return isConcerned;
	}

	public void setConcerned(boolean concerned) {
		isConcerned = concerned;
	}
}

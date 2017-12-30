package com.sysu.pro.fade.home.event;

/**
 * 信息流item界面变动的事件
 * Created by LaiXiancheng on 2017/12/30.
 * Email: lxc.sysu@qq.com
 */

public class itemChangeEvent {
	int position;

	public itemChangeEvent(int position) {
		this.position = position;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}
}

package com.sysu.pro.fade.beans;

import java.io.Serializable;

public class PushMessage implements Serializable{
	//个推 推送所用的bean
	private Object obj; //“进度贡献”的时候为一个Note，“新的粉丝”的时候为一个User，“评论”的时候为一个Comment
	private Integer msgId;//1为续秒通知，2为评论通知，3为粉丝通知

	public PushMessage(){}

	public PushMessage(Object obj, Integer msgId) {
		super();
		this.obj = obj;
		this.msgId = msgId;
	}
	public Object getObj() {
		return obj;
	}
	
	
	public void setObj(Object obj) {
		this.obj = obj;
	}
	public Integer getMsgId() {
		return msgId;
	}
	public void setMsgId(Integer msgId) {
		this.msgId = msgId;
	}
	
	
}

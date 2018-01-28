package com.sysu.pro.fade.beans;

/**
 * Created by LaiXiancheng on 2018/1/24.
 * Email: lxc.sysu@qq.com
 */

public class RongCloudResponse {
	int code = 200;
	String token = "";
	String userId = "";

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}
}

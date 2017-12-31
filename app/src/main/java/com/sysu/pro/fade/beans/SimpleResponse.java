package com.sysu.pro.fade.beans;

import java.util.Map;

public class SimpleResponse {
	private String success;
	private String err;
	private Map<String, Object> extra; //多余的信息以json格式放到这里面
	
	public SimpleResponse() {
	}
	
	public SimpleResponse(String success,String err) {
		this.err = err;
		this.success = success;
	}
	
	public SimpleResponse(String success,String err,Map<String, Object>extra) {
		this.err = err;
		this.success = success;
		this.extra = extra;
	}

	private long fetchTime;

	public long getFetchTime() {
		return fetchTime;
	}

	public void setFetchTime(long fetchTime) {
		this.fetchTime = fetchTime;
	}
	
	
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getErr() {
		return err;
	}
	public void setErr(String err) {
		this.err = err;
	}

	public Map<String, Object> getExtra() {
		return extra;
	}

	public void setExtra(Map<String, Object> extra) {
		this.extra = extra;
	}


	
	
}

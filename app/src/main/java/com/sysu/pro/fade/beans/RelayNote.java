package com.sysu.pro.fade.beans;

import java.io.Serializable;
import java.util.List;

/**
 * Created by LaiXiancheng on 2017/7/30.
 * 转发内容的最小单位
 */

public class RelayNote implements Serializable {
	/**
	 * 转发的文字内容
	 */
	String name;         //用户名
	int user_id;        //用户id
	String content;     //帖子内容

	private List<String> imgUrls;        //图片url数组
	private List<Double> imgSizes;       //图片尺寸数组

	//add by hl 2017.9.14
	private List<String> imgCoordinates;//图片左上角的坐标，其中一项的形式 "x:y"
	private Integer imgCutSize;          //裁剪比例 1代表长图4:5, 2宽图15:8  0代表之前那些没设置比例的

	public List<String> getImgUrls() {
		return imgUrls;
	}

	public void setImgUrls(List<String> imgUrls) {
		this.imgUrls = imgUrls;
	}

	public List<Double> getImgSizes() {
		return imgSizes;
	}

	public void setImgSizes(List<Double> imgSizes) {
		this.imgSizes = imgSizes;
	}

	public RelayNote(){
		/**
		 * 默认构造方法
		 */
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public RelayNote(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getImgCutSize() {
		return imgCutSize;
	}

	public void setImgCutSize(Integer imgCutSize) {
		this.imgCutSize = imgCutSize;
	}

	public List<String> getImgCoordinates() {
		return imgCoordinates;
	}

	public void setImgCoordinates(List<String> imgCoordinates) {
		this.imgCoordinates = imgCoordinates;
	}
}

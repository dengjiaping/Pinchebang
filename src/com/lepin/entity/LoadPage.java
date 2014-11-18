package com.lepin.entity;

import java.io.Serializable;

public class LoadPage implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int loadPageId;
	private String url;
	private String imgUrl;// 图片下载路径
	private long updateTime = 0;// 最后更新时间
	private int state;

	public int getLoadPageId() {
		return loadPageId;
	}

	public String getUrl() {
		return url;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public int getState() {
		return state;
	}

	public void setLoadPageId(int loadPageId) {
		this.loadPageId = loadPageId;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	public void setState(int state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "LoadPage [loadPageId=" + loadPageId + ", url=" + url + ", imgUrl=" + imgUrl
				+ ", updateTime=" + updateTime + ", state=" + state + "]";
	}

}

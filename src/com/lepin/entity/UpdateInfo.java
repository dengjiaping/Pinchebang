package com.lepin.entity;

public class UpdateInfo {

	private String path = null;

	private String state = null;

	private String version = null;//versionName

	private int versionCode = -1;

	private int versionId = -1;

	private String versionLog = null;

	private String wxAddr = null;

	public String getPath() {
		return path;
	}

	public String getState() {
		return state;
	}

	public String getVersion() {
		return version;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public int getVersionId() {
		return versionId;
	}

	public String getVersionLog() {
		return versionLog;
	}

	public String getWxAddr() {
		return wxAddr;
	}

}

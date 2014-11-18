package com.lepin.entity;

import java.io.Serializable;

public class MyAddr implements Serializable {

	/**
	 * 常用地址实体类
	 */
	private static final long serialVersionUID = 1L;

	private int myAddrId;
	private int userId;
	private String cityId;
	private String addrType;// 地址类型
	public final static String ADDRTYPE_FAMILY = "FAMILY";
	public final static String ADDRTYPE_COMPANY = "COMPANY";
	private String name;
	private long lat;
	private long lon;

	public int getMyAddrId() {
		return myAddrId;
	}

	public int getUserId() {
		return userId;
	}

	public String getCityId() {
		return cityId;
	}

	public String getAddrType() {
		return addrType;
	}

	public String getName() {
		return name;
	}

	public long getLat() {
		return lat;
	}

	public long getLon() {
		return lon;
	}

	public void setMyAddrId(int myAddrId) {
		this.myAddrId = myAddrId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setLat(long lat) {
		this.lat = lat;
	}

	public void setLon(long lon) {
		this.lon = lon;
	}

	@Override
	public String toString() {
		return "MyAddr [myAddrId=" + myAddrId + ", userId=" + userId + ", cityId=" + cityId
				+ ", addrType=" + addrType + ", name=" + name + ", lat=" + lat + ", lon=" + lon
				+ "]";
	}

}

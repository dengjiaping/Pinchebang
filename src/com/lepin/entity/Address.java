package com.lepin.entity;

import java.io.Serializable;

/**
 * 百度地图联想搜索地址实体类
 * 
 */
public class Address implements Serializable {

	private static final long serialVersionUID = 1L;

	private String address;
	private String city;
	private String name;
	private long x;//经度
	private long y;//纬度

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getX() {
		return x;
	}

	public void setX(long x) {
		this.x = x;
	}

	public long getY() {
		return y;
	}

	public void setY(long y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return "Address [address=" + address + ", city=" + city + ", name=" + name + ", x=" + x
				+ ", y=" + y + "]";
	}

}

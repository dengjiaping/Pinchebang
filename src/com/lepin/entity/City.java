package com.lepin.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 百度地图城市类
 * 
 * @author Administrator
 * 
 */

public class City implements Serializable {

	private static final long serialVersionUID = 1L;

	private int area_id;
	private String area_name;
	private int orderid;
	private int pid;

	private ArrayList<City> subAreas;// 下级城市

	public int getArea_id() {
		return area_id;
	}

	public String getArea_name() {
		return area_name;
	}

	public int getOrderid() {
		return orderid;
	}

	public int getPid() {
		return pid;
	}

	public ArrayList<City> getSubCitys() {
		return subAreas;
	}

	public void setArea_id(int area_id) {
		this.area_id = area_id;
	}

	public void setArea_name(String area_name) {
		this.area_name = area_name;
	}

	public void setOrderid(int orderid) {
		this.orderid = orderid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public void setSubCitys(ArrayList<City> subCitys) {
		this.subAreas = subCitys;
	}

	@Override
	public String toString() {
		return "City [area_id=" + area_id + ", area_name=" + area_name + ", pid=" + pid
				+ ", orderid=" + orderid + ", subAreas=" + subAreas + "]";
	}


}

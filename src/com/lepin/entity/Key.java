package com.lepin.entity;

import java.io.Serializable;

/**
 * 搜索条件
 * 
 */
public class Key implements Serializable {

	private static final long serialVersionUID = 1L;
	private String carpoolType;// 长途或者上下班
	private String start_name;
	private String end_name;
	private long start_lat;
	private long start_lon;
	private long end_lat;
	private long end_lon;
	private int radius;//半径
	private String startCityId;
	private String endCityId;
	private String currDistrict;

	public Key() {

	}

	public long getStart_lat() {
		return start_lat;
	}

	public long getStart_lon() {
		return start_lon;
	}

	public int getRadius() {
		return radius;
	}


	public void setRadius(int radius) {
		this.radius = radius;
	}


	public String getStartCityId() {
		return startCityId;
	}

	public String getEndCityId() {
		return endCityId;
	}

	public void setStartCityId(String startCityId) {
		this.startCityId = startCityId;
	}

	public void setEndCityId(String endCityId) {
		this.endCityId = endCityId;
	}

	public long getEnd_lat() {
		return end_lat;
	}

	public long getEnd_lon() {
		return end_lon;
	}

	public String getCarpoolType() {
		return carpoolType;
	}

	public void setCarpoolType(String carpool_type) {
		this.carpoolType = carpool_type;
	}

	public String getStart_name() {
		return start_name;
	}

	public void setStart_name(String start_name) {
		this.start_name = start_name;
	}

	public String getEnd_name() {
		return end_name;
	}

	public void setEnd_name(String end_name) {
		this.end_name = end_name;
	}


	public void setStart_lat(long start_lat) {
		this.start_lat = start_lat;
	}

	public void setStart_lon(long start_lon) {
		this.start_lon = start_lon;
	}

	public void setEnd_lat(long end_lat) {
		this.end_lat = end_lat;
	}

	public void setEnd_lon(long end_lon) {
		this.end_lon = end_lon;
	}


	public String getCurrDistrict() {
		return currDistrict;
	}

	public void setCurrDistrict(String currDistrict) {
		this.currDistrict = currDistrict;
	}

	@Override
	public String toString() {
		return "Key [carpoolType=" + carpoolType + ", start_name=" + start_name + ", end_name="
				+ end_name + ", start_lat=" + start_lat + ", start_lon=" + start_lon + ", end_lat="
				+ end_lat + ", end_lon=" + end_lon + ", radius=" + radius + ", startCityId="
				+ startCityId + ", endCityId=" + endCityId + ", currDistrict=" + currDistrict + "]";
	}


}

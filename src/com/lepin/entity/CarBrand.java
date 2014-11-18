package com.lepin.entity;

import java.io.Serializable;

public class CarBrand implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Integer carBrandId;
	private String carBrandName;

	private String initials;

	private String state;
	public final static String STATE_AVAILABLE = "AVAILABLE";
	public final static String STATE_UNAVAILABLE = "UNAVAILABLE";

	public Integer getCarBrandId() {
		return carBrandId;
	}

	public String getCarBrandName() {
		return carBrandName;
	}

	public void setCarBrandId(Integer carBrandId) {
		this.carBrandId = carBrandId;
	}

	public void setCarBrandName(String carBrandName) {
		this.carBrandName = carBrandName;
	}

	public void setInitials(String initials) {
		this.initials = initials;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getInitials() {
		return initials;
	}

	public String getState() {
		return state;
	}

	@Override
	public String toString() {
		return "{\"carBrandId\":" + carBrandId + ", \"carBrandName\":" + carBrandName
				+ ", \"initials\":" + initials + ", \"state\":" + state + "}";
	}
	
}

package com.lepin.entity;

import java.io.Serializable;

public class CarModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String car_brand_id;
	private String orderid;
	private String car_type_id;
	private String car_type_name;

	public String getCar_brand_id() {
		return car_brand_id;
	}

	public void setCar_brand_id(String car_brand_id) {
		this.car_brand_id = car_brand_id;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getCar_type_id() {
		return car_type_id;
	}

	public void setCar_type_id(String car_type_id) {
		this.car_type_id = car_type_id;
	}

	public String getCar_type_name() {
		return car_type_name;
	}

	public void setCar_type_name(String car_type_name) {
		this.car_type_name = car_type_name;
	}

	@Override
	public String toString() {
		return "CarModel [car_brand_id=" + car_brand_id + ", orderid=" + orderid + ", car_type_id="
				+ car_type_id + ", car_type_name=" + car_type_name + "]";
	}

}

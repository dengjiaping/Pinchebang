package com.lepin.entity;

import java.io.Serializable;

import android.content.Context;
import android.text.TextUtils;

import com.lepin.activity.R;

public class CarType implements Serializable {

	private static final long serialVersionUID = 1L;
	private int carTypeId;// 车辆型号编号
	private int carBrandId;// 车辆品牌编号
	private String carTypeName;
	private int orderId;
	private CarBrand brand;

	/*
	 * private String state; public final static String STATE_AVAILABLE =
	 * "AVAILABLE"; public final static String STATE_UNAVAILABLE =
	 * "UNAVAILABLE";
	 */

	public CarType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CarType(String carTypeName) {
		super();
		this.carTypeName = carTypeName;
	}

	public int getCarTypeId() {
		return carTypeId;
	}

	public String getCarTypeName() {
		return carTypeName;
	}

	public String getCarTypeName(Context mContext) {
		return (TextUtils.isEmpty(carTypeName)) ? mContext.getString(R.string.unknow) : carTypeName;
	}

	public int getCarBrandId() {
		return carBrandId;
	}

	public int getOrderId() {
		return orderId;
	}

	public CarBrand getBrand() {
		return brand;
	}

	public void setCarTypeId(int carTypeId) {
		this.carTypeId = carTypeId;
	}

	public void setCarTypeName(String carTypeName) {
		this.carTypeName = carTypeName;
	}

	public void setCarBrandId(int carBrandId) {
		this.carBrandId = carBrandId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public void setBrand(CarBrand brand) {
		this.brand = brand;
	}

	@Override
	public String toString() {
		return "CarType [carTypeId=" + carTypeId + ", carBrandId=" + carBrandId + ", carTypeName="
				+ carTypeName + ", orderId=" + orderId + ", brand=" + brand + "]";
	}

}

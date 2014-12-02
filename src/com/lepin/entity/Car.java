package com.lepin.entity;

import java.io.Serializable;

import android.content.Context;
import android.text.TextUtils;

import com.lepin.activity.R;

/**
 * 车辆信息实体
 */
public class Car implements Serializable {

	private static final long serialVersionUID = 1L;
	public final static String CARSIZE_SMALL_CAR = "SMALL_CAR";
	public final static String CARSIZE_MIDSIZE_CAR = "MIDSIZE_CAR";
	public final static String CARSIZE_LARGE_CAR = "LARGE_CAR";
	public final static String CARSIZE_OTHER = "OTHER";

	public final static String STATE_WAIT_AUDIT = "WAIT_AUDIT";
	public final static String STATE_AUDITING = "AUDITING";
	public final static String STATE_AUDITED = "AUDITED";
	public final static String STATE_AUDIT_UNPASS = "AUDIT_UNPASS";
	public final static String STATE_DELETE = "DELETE";
	private int carId;// 车id
	private int userId;
	private String carSize;// 车型
	private String carTypeId;// 车辆型号编号
	private String carNum;// 车架号
	private String state;// 车辆状态

	private CarType carType;// 车型
	private String info;// 车辆信息
	private String licence;// 牌照
	private String num;// 人数 (1-6)

	public Car() {
		super();
	}

	public int getCarId() {
		return carId;
	}

	public int getUserId() {
		return userId;
	}

	public String getCarSize() {
		return carSize;
	}

	public String getCarTypeId() {
		return carTypeId;
	}

	public String getCarNum() {
		return carNum;
	}

	public String getCarNum(Context mContext) {
		return TextUtils.isEmpty(carNum) ? mContext.getString(R.string.not_setting) : carNum;
	}

	public String getState() {
		return state;
	}

	public CarType getCarType() {
		return carType;
	}

	public String getInfo() {
		return info;
	}

	public String getLicence() {
		return licence;
	}

	public String getLicence(Context mContext) {
		return TextUtils.isEmpty(licence) ? mContext.getString(R.string.my_car_vefic_card) + ":"
				+ mContext.getString(R.string.not_setting) : licence;
	}

	public String getNum() {
		return num;
	}

	public void setCarId(int carId) {
		this.carId = carId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setCarSize(String carType) {
		this.carSize = carType;
	}

	public void setCarTypeId(String carTypeId) {
		this.carTypeId = carTypeId;
	}

	public void setCarNum(String carNum) {
		this.carNum = carNum;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setCarType(CarType carType) {
		this.carType = carType;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public void setLicence(String licence) {
		this.licence = licence;
	}

	public void setNum(String num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "Car [carId=" + carId + ", userId=" + userId + ", carSize=" + carSize
				+ ", carTypeId=" + carTypeId + ", carNum=" + carNum + ", state=" + state
				+ ", carType=" + carType + ", info=" + info + ", licence=" + licence + ", num="
				+ num + "]";
	}

}

package com.lepin.entity;

import java.io.Serializable;

import android.content.Context;
import android.text.TextUtils;

import com.lepin.activity.R;
import com.lepin.util.TimeUtils;

/**
 * 拼车信息实体
 * 
 */
public class Pinche implements Serializable {

	public final static String CARPOOLTYPE_LONG_TRIP = "LONG_TRIP";
	public final static String CARPOOLTYPE_ON_OFF_WORK = "ON_OFF_WORK";
	public final static String PASSENGER = "PASSENGER";// 乘客
	public final static String DRIVER = "DRIVER";// 司机
	private static final long serialVersionUID = 1L;

	public final static String STATE_COLSE = "COLSE";

	public final static String STATE_DELETE = "DELETE";
	public final static String STATE_NORMAL = "NORMAL";
	private long backTime = 0;// 返回时间
	private String book_num;// 已经预约人数
	private boolean booking;// 是否预约

	private Car car;
	private int carId;// 车辆id
	private String carpoolType;// 类型（长途，上下班）
	private int charge;// 费用（每人）

	private int cityId;
	private CycleDate cycle;// 时间段（只有上下班才有）
	private long departureTime;// 出发时间
	private Integer endLat = -1;
	private Integer endLon = -1;
	private String endName;// 目的地
	private String info;// 车况

	private int infoId;// 信息id

	private String infoType;// FIND_PASSENGERS|FIND_DRIVER
	private boolean isExpired = false;
	private String note;// 备注
	private Integer num = 0;// 人数
	private Integer startLat = -1;// 起点维度
	private Integer startLon = -1;// 起点经度

	private String startName;// 起点

	private String state;

	private String tel;// 电话

	private User user;

	private int userId;// 用户id

	private int startCityId;

	private int endCityId;

	private Point[] points;// 途径点集合

	public Point[] getPoints() {
		return points;
	}

	public void setPoints(Point[] points) {
		this.points = points;
	}

	public int getStartCityId() {
		return startCityId;
	}

	public void setStartCityId(int startCityId) {
		this.startCityId = startCityId;
	}

	public int getEndCityId() {
		return endCityId;
	}

	public void setEndCityId(int endCityId) {
		this.endCityId = endCityId;
	}

	public Pinche() {// 构造器
		super();
	}

	public String getBackTime() {
		if (backTime != 0) return TimeUtils.secondToDate(backTime);
		return "";
	}

	public String getBook_num() {
		return book_num;
	}

	public Car getCar() {
		return car;
	}

	public int getCar_id() {
		return carId;
	}

	public String getCarpoolType() {
		return carpoolType;
	}

	public int getCharge() {
		return charge;
	}

	public int getCityId() {
		return cityId;
	}

	public CycleDate getCycle() {
		return cycle;
	}

	public String getDepartureTime() {

		return (TextUtils.isEmpty(carpoolType) || carpoolType.equals("null")) ? "" : carpoolType
				.equals(CARPOOLTYPE_LONG_TRIP) ? TimeUtils.formatDate(departureTime * 1000,
				"yyyy-MM-dd HH:mm") : TimeUtils.secondToDate(departureTime);
	}

	public String getEnd_name() {
		return endName;
	}

	public String getEnd_name(Context mContext) {
		return TextUtils.isEmpty(endName) ? mContext.getString(R.string.not_setting) : endName;
	}

	public Integer getEndLat() {
		return endLat;
	}

	public Integer getEndLon() {
		return endLon;
	}

	public String getInfo() {
		return info;
	}

	public int getInfo_id() {
		return infoId;
	}

	public String getInfoType() {
		return infoType;
	}

	public String getNote() {
		return note;
	}

	public Integer getNum() {
		return num;
	}

	public String getStart_name() {
		return TextUtils.isEmpty(startName) ? "匿名" : startName;
	}

	public String getStart_name(Context mContext) {
		return TextUtils.isEmpty(startName) ? mContext.getString(R.string.not_setting) : startName;
	}

	public Integer getStartLat() {
		return startLat;
	}

	public Integer getStartLon() {
		return startLon;
	}

	public String getState() {
		return state;
	}

	public String getTel() {
		return tel;
	}

	public User getUser() {
		return user;
	}

	public int getUser_id() {
		return userId;
	}

	public boolean isBooking() {
		return booking;
	}

	public boolean isExpired() {
		return isExpired;
	}

	public void setBook_num(String book_num) {
		if (!TextUtils.isEmpty(book_num)) {
			this.book_num = book_num;
		}
	}

	public void setBooking(boolean booking) {
		this.booking = booking;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public void setCar_id(int car_id) {
		this.carId = car_id;
	}

	public void setCarpoolType(String carpoolType) {
		this.carpoolType = carpoolType;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public void setCityId(int cityId) {
		this.cityId = cityId;
	}

	public void setDepartureTime(long departureTime) {
		this.departureTime = departureTime;
	}

	public void setEnd_name(String end_name) {
		if (!TextUtils.isEmpty(end_name)) {
			this.endName = end_name;
		}
	}

	public void setEndLat(Integer endLat) {
		this.endLat = endLat;
	}

	public void setEndLon(Integer endLon) {
		this.endLon = endLon;
	}

	public void setExpired(boolean isExpired) {
		this.isExpired = isExpired;
	}

	public void setInfo(String info) {
		if (!TextUtils.isEmpty(info)) {
			this.info = info;
		}
	}

	public void setInfo_id(int info_id) {
		this.infoId = info_id;
	}

	public void setInfoType(String infoType) {
		this.infoType = infoType;
	}

	public void setNote(String note) {
		if (!TextUtils.isEmpty(note)) {
			this.note = note;
		}
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setStart_name(String start_name) {
		if (!TextUtils.isEmpty(start_name)) {
			this.startName = start_name;
		}
	}

	public void setStartLat(Integer startLat) {
		this.startLat = startLat;
	}

	public void setStartLon(Integer startLon) {
		this.startLon = startLon;
	}

	public void setState(String state) {
		if (!TextUtils.isEmpty(state)) {
			this.state = state;
		}
	}

	public void setTel(String tel) {
		if (!TextUtils.isEmpty(tel)) {
			this.tel = tel;
		}
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setUser_id(int user_id) {
		this.userId = user_id;
	}

	@Override
	public String toString() {
		return "Pinche [backTime=" + backTime + ", book_num=" + book_num + ", booking=" + booking
				+ ", car=" + car + ", carId=" + carId + ", carpoolType=" + carpoolType
				+ ", charge=" + charge + ", cityId=" + cityId + ", cycle=" + cycle
				+ ", departureTime=" + departureTime + ", endLat=" + endLat + ", endLon=" + endLon
				+ ", endName=" + endName + ", info=" + info + ", infoId=" + infoId + ", infoType="
				+ infoType + ", isExpired=" + isExpired + ", note=" + note + ", num=" + num
				+ ", startLat=" + startLat + ", startLon=" + startLon + ", startName=" + startName
				+ ", state=" + state + ", tel=" + tel + ", user=" + user + ", userId=" + userId
				+ "]";
	}

}

package com.lepin.entity;

import java.io.Serializable;

import com.lepin.util.Util;
import com.lepin.util.TimeUtils;

/**
 * 拼车详情实体
 */
public class PincheDetails implements Serializable{

	private static final long serialVersionUID = 1L;
	private int car_id;// 车ID
	private String tel;// 电话
	private int state;// 车状态
	private String email_auth;// 邮箱是否验证
	private String tel_auth;// 电话是否验证
	private int info_id;// 详情ID
	private int user_id;// 发布者ID
	private String end_name;// 起点
	private String start_name;// 终点
	private String username;// 昵称
	private String info;// 车信息
	private String note;// 备注
	private String departure_time;// 出发时间
	private String back_time;// 返回时间
	private String cycle;// 时间段（只有上下班才有）
	private int charge;// 费用
	private String car_model;// 车型
	private int num;// 人数
	private int gender;// 性别
	private int info_type;// 0表示司机，1表示乘客
	private int carpool_type;// 0表示长途，1表示上下班

	private int book_num;//
	private String book_id;// 预约id为空表示为预约，否则为预约编号
	private String licence;// 车牌号
	private Util util = Util.getInstance();
	private boolean is_book = false;// 是否已经预约

	public boolean is_book() {
		return is_book;
	}

	public void set_book(boolean isBook) {
		this.is_book = isBook;
	}

	public PincheDetails() {
	}

	public String getCycle() {
		return cycle;
	}

	public void setCycle(String cycle) {
		if (!util.isNullOrEmpty(cycle)) {
			if (getCarpool_type() == 1) {// 上下班
				this.cycle = cycle;
			}
		}
	}

	public String getDeparture_time() {
		return departure_time;
	}

	public void setDeparture_time(String departure_time) {
		if (!util.isNullOrEmpty(departure_time) && !"0".equals(departure_time)) {
			if (carpool_type == 0) {// 长途
				// 时间秒数转换为指定格式
				this.departure_time = TimeUtils.formatDate(Long.parseLong(departure_time) * 1000,
						"yyyy-MM-dd HH:mm");
			} else {// 上下班
				this.departure_time = TimeUtils.secondToDate(Long.parseLong(departure_time));
			}
		} else {
			this.departure_time = "";
		}
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		if (!util.isNullOrEmpty(username)) {
			this.username = username;
		}
	}

	public String getBack_time() {
		return back_time;
	}

	public void setBack_time(String back_time) {
		if (!util.isNullOrEmpty(back_time) && !"0".equals(back_time)) {
			if (carpool_type == 0) {// 长途
				// 时间秒数转换为指定格式
				this.back_time = TimeUtils.formatDate(Long.parseLong(back_time) * 1000,
						"yyyy-MM-dd HH:mm");
			} else {// 上下班
				this.back_time = TimeUtils.secondToDate(Long.parseLong(back_time));
			}
		} else {
			this.back_time = "";
		}
	}

	public int getBook_num() {
		return book_num;
	}

	public void setBook_num(int book_num) {
		this.book_num = book_num;
	}

	public int getCar_id() {
		return car_id;
	}

	public void setCar_id(int car_id) {
		this.car_id = car_id;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		if (!util.isNullOrEmpty(tel)) {
			this.tel = tel;
		}
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getEmail_auth() {
		return email_auth;
	}

	public void setEmail_auth(String email_auth) {
		if (!util.isNullOrEmpty(email_auth)) {
			this.email_auth = email_auth;
		}
	}

	public String getTel_auth() {
		return tel_auth;
	}

	public void setTel_auth(String tel_auth) {
		if (!util.isNullOrEmpty(tel_auth)) {
			this.tel_auth = tel_auth;
		}
	}

	public int getInfo_id() {
		return info_id;
	}

	public void setInfo_id(int info_id) {
		this.info_id = info_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getEnd_name() {
		return end_name;
	}

	@Override
	public String toString() {
		return "PincheDetails [car_id=" + car_id + ", tel=" + tel + ", state=" + state
				+ ", email_auth=" + email_auth + ", tel_auth=" + tel_auth + ", info_id=" + info_id
				+ ", user_id=" + user_id + ", end_name=" + end_name + ", start_name=" + start_name
				+ ", username=" + username + ", info=" + info + ", note=" + note
				+ ", departure_time=" + departure_time + ", back_time=" + back_time + ", cycle="
				+ cycle + ", charge=" + charge + ", car_model=" + car_model + ", num=" + num
				+ ", gender=" + gender + ", info_type=" + info_type + ", carpool_type="
				+ carpool_type + ", book_num=" + book_num + ", book_id=" + book_id + ", licence="
				+ licence + ", isBook=" + is_book + "]";
	}

	public void setEnd_name(String end_name) {
		if (!util.isNullOrEmpty(end_name)) {
			this.end_name = end_name;
		}
	}

	public String getStart_name() {
		return start_name;
	}

	public void setStart_name(String start_name) {
		if (!util.isNullOrEmpty(start_name)) {
			this.start_name = start_name;
		}
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		if (!util.isNullOrEmpty(info)) {
			this.info = info;
		}
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public int getCharge() {
		return charge;
	}

	public void setCharge(int charge) {
		this.charge = charge;
	}

	public String getCar_model() {
		return car_model;
	}

	public void setCar_model(String car_model) {
		if (!util.isNullOrEmpty(car_model)) {
			this.car_model = car_model;
		}
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public int getInfo_type() {
		return info_type;
	}

	public void setInfo_type(int info_type) {
		this.info_type = info_type;
	}

	public int getCarpool_type() {
		return carpool_type;
	}

	public void setCarpool_type(int carpool_type) {
		this.carpool_type = carpool_type;
	}

	public String getBook_id() {
		return book_id;
	}

	public void setBook_id(String book_id) {
		if (!util.isNullOrEmpty(book_id)) {
			this.book_id = book_id;
		}
	}

	public String getLicence() {
		return licence;
	}

	public void setLicence(String licence) {
		if (!util.isNullOrEmpty(licence)) {
			String head = licence.substring(0, 2);
			String tail = licence.substring(licence.length() - 3, licence.length());
			this.licence = head + "**" + tail;
		}
	}

}
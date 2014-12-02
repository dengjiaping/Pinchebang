package com.lepin.entity;

import java.io.Serializable;

import com.lepin.util.TimeUtils;

/**
 * 我的预约/我的信息实体
 * 
 */
public class Book implements Serializable {

	private static final long serialVersionUID = 1L;

	private int infoOrderId;
	private int passengerId;// 乘客
	private int driverId;// 车主
	private int infoId;
	private int price;
	private int num;
	private String snapshot;
	private String state;
	public final static String STATE_CANCEL = "CANCEL";
	public final static String STATE_NEW = "NEW";// 待处理
	public final static String STATE_CONFIRM = "CONFIRM";// 待付款
	public final static String STATE_COMPLETE = "COMPLETE";// 已经完成
	public final static String STATE_PAYMENT = "PAYMENT";// 已支付
	public final static String STATE_WAITING_PROCESS = "WAITING_PROCESS";// 待处理，司机端使用

	private String payType;// 线下支付、线上支付

	private String bookType;// DRIVER,PASSENGER 表示信息是由谁发起的

	private String note;// 用户备注
	private String sysNote;// 系统备注

	private String deleteByPassenger;// NO,YES 乘客删除
	public final static String DELETE_BY_PASSENGER_NO = "NO";
	public final static String DELETE_BY_PASSENGER_YES = "YES";

	private String deleteByDriver;// 司机删除
	public final static String DELETE_BY_DRIVER_NO = "NO";
	public final static String DELETE_BY_DRIVER_YES = "YES";

	private User driver;
	private User passenger;

	private long createTime = -1;

	private long rideTime; // 订单出发时间

	public long getRideTime() {
		return rideTime;
	}

	public void setRideTime(long rideTime) {
		this.rideTime = rideTime;
	}

	public String getCreateTime() {
		return TimeUtils.formatDate(createTime, TimeUtils.TIME_FORMART_HMS);
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public User getPassenger() {
		return passenger;
	}

	public void setPassenger(User passenger) {
		this.passenger = passenger;
	}

	public User getDriver() {
		return driver;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}

	public String getDeleteByDriver() {
		return deleteByDriver;
	}

	public void setDeleteByDriver(String deleteByDriver) {
		this.deleteByDriver = deleteByDriver;
	}

	public int getInfoOrderId() {
		return infoOrderId;
	}

	public String getBookType() {
		return bookType;
	}

	public int getDriverId() {
		return driverId;
	}

	public String getDeleteByPassenger() {
		return deleteByPassenger;
	}

	public int getInfoId() {
		return infoId;
	}

	public String getNote() {
		return note;
	}

	public int getNum() {
		return num;
	}

	public String getPayType() {
		return payType;
	}

	public int getPrice() {
		return price;
	}

	public String getSnapshot() {
		return snapshot;
	}

	public String getState() {
		return state;
	}

	/**
	 * 根据类型获取预定的字符描述
	 * 
	 * @param type
	 *            0表示预约人，1表示（信息发布人）
	 * @return 字符描述
	 */
	public String getStateName(int type, int currstate) {
		String str = "";
		if (type == 0) {// 预约人
			switch (currstate) {
			case -5:
				str = "已删除";
				break;// 双方已删除
			case -4:
				str = "已取消";
				break;// 发布人已删除
			case -3:
				str = "已删除";
				break;// 预约人删除
			case -2:
				str = "已取消";
				break;// 发布人取消
			case -1:
				str = "已取消";
				break;// 预约人取消
			case 0:
				str = "待确认";
				break;
			case 1:
				str = "已确认";
				break;
			case 2:
				str = "完成拼车";
				break;
			case 3:
				str = "已评价";
				break;// 预约人评价
			case 4:
				str = "未评价";
				break;// 发布人评价
			case 5:
				str = "已互评";
				break;
			}
		} else {// 信息发布人
			switch (currstate) {
			case -5:
				str = "已删除";
				break;// 双方已删除
			case -4:
				str = "已删除";
				break;// 发布人已删除
			case -3:
				str = "已取消";
				break;// 预约人删除
			case -2:
				str = "已取消";
				break;// 发布人取消
			case -1:
				str = "已取消";
				break;// 预约人取消
			case 0:
				str = "待确认";
				break;
			case 1:
				str = "已确认";
				break;
			case 2:
				str = "完成拼车";
				break;
			case 3:
				str = "未评价";
				break;// 预约人评价
			case 4:
				str = "已评价";
				break;// 发布人评价
			case 5:
				str = "已互评";
				break;
			}
		}
		return str;
	}

	public String getSysNote() {
		return sysNote;
	}

	public int getPassengerId() {
		return passengerId;
	}

	public void setInfoOrderId(int bookId) {
		this.infoOrderId = bookId;
	}

	public void setBook_type(String bookType) {
		this.bookType = bookType;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public void setDeleteByPassenger(String deleteByPassenger) {
		this.deleteByPassenger = deleteByPassenger;
	}

	public void setInfoId(int info_id) {
		this.infoId = info_id;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setPayType(String paytype) {
		this.payType = paytype;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public void setSnapshot(String snapshot) {
		this.snapshot = snapshot;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setSys_note(String sys_note) {
		this.sysNote = sys_note;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	@Override
	public String toString() {
		return "Book [infoOrderId=" + infoOrderId + ", passengerId=" + passengerId + ", driverId=" + driverId
				+ ", infoId=" + infoId + ", price=" + price + ", num=" + num + ", snapshot="
				+ snapshot + ", state=" + state + ", payType=" + payType + ", bookType=" + bookType
				+ ", note=" + note + ", sysNote=" + sysNote + ", deleteByPassenger="
				+ deleteByPassenger + ", deleteByDriver=" + deleteByDriver + ", driver=" + driver
				+ ", passenger=" + passenger + "]";
	}

}

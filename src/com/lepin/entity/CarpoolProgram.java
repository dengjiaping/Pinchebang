package com.lepin.entity;

import java.io.Serializable;
import java.util.List;

public class CarpoolProgram implements Serializable {

	/**
	 * 司机拼车计划
	 */
	private static final long serialVersionUID = 1L;
	private int carpoolProgramId;
	private int infoId;
	private int driverId;
	private long departureTime;// 出发时间
	private long backTime;// 返程时间
	private CycleDate cycle;// 周期
	private Integer charge;// 费用
	private String state;
	public static final String STATE_DELETED = "DELETE";
	public static final String STATE_NORMAL = "NORMAL";
	public static final String STATE_PAUSE = "PAUSE";
	private String note;
	private String descrip;

	private List<CarpoolProgramPassenger> carpoolProgramPassengers;// 拼车计划中的乘客列表
	private User driver; // 车主对象
	private int programDate;// 计划拼车的日期，下一次拼车的时间，如20130731
	private Pinche info;

	public Pinche getInfo() {
		return info;
	}

	public void setInfo(Pinche info) {
		this.info = info;
	}

	public int getCarpoolProgramId() {
		return carpoolProgramId;
	}

	public int getInfoId() {
		return infoId;
	}

	public int getDriverId() {
		return driverId;
	}

	public long getDepartureTime() {
		return departureTime;
	}

	public long getBackTime() {
		return backTime;
	}

	public Integer getCharge() {
		return charge;
	}

	public String getState() {
		return state;
	}

	public String getNote() {
		return note;
	}

	public String getDescrip() {
		return descrip;
	}

	public List<CarpoolProgramPassenger> getCarpoolProgramPassengers() {
		return carpoolProgramPassengers;
	}

	public User getDriver() {
		return driver;
	}

	public int getProgramDate() {
		return programDate;
	}

	public void setCarpoolProgramId(int carpoolProgramId) {
		this.carpoolProgramId = carpoolProgramId;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}

	public void setDepartureTime(long departureTime) {
		this.departureTime = departureTime;
	}

	public void setBackTime(long backTime) {
		this.backTime = backTime;
	}

	public CycleDate getCycle() {
		return cycle;
	}

	public void setCycle(CycleDate cycle) {
		this.cycle = cycle;
	}

	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public void setCarpoolProgramPassengers(List<CarpoolProgramPassenger> carpoolProgramPassengers) {
		this.carpoolProgramPassengers = carpoolProgramPassengers;
	}

	public void setDriver(User driver) {
		this.driver = driver;
	}

	public void setProgramDate(int programDate) {
		this.programDate = programDate;
	}

	@Override
	public String toString() {
		return "CarpoolProgram [carpoolProgramId=" + carpoolProgramId + ", infoId=" + infoId
				+ ", driverId=" + driverId + ", departureTime=" + departureTime + ", backTime="
				+ backTime + ", cycle=" + cycle + ", charge=" + charge + ", state=" + state
				+ ", note=" + note + ", descrip=" + descrip + ", carpoolProgramPassengers="
				+ carpoolProgramPassengers + ", driver=" + driver + ", programDate=" + programDate
				+ "]";
	}

}

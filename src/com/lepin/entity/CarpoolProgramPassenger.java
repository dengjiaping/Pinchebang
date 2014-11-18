package com.lepin.entity;

import java.io.Serializable;

public class CarpoolProgramPassenger implements Serializable {

	/**
	 * 乘客拼车计划
	 */
	private static final long serialVersionUID = 1L;
	private int carpoolProgramPassengerId;
	private int carpoolProgramId;// 计划编号
	private int passengerId;// 乘客编号
	private long addTime;// 乘客加入计划的时间
	private int infoId;
	private int carpoolTimes;// 拼车次数
	private int unsettledTimes;// 拼车未结算次数
	private String state;
	private String descrip;
	private User passenger;// 乘客对象
	private int programDate;// 计划拼车的日期，下一次拼车的时间，如20130731
	private Pinche info;// 拼车详情

	private int[] carpoolCalendar;

	private CarpoolProgram carpoolProgram;

	public int[] getCarpoolCalendar() {
		return carpoolCalendar;
	}

	public CarpoolProgram getCarpoolProgram() {
		return carpoolProgram;
	}

	public void setCarpoolCalendar(int[] carpoolCalendar) {
		this.carpoolCalendar = carpoolCalendar;
	}

	public void setCarpoolProgram(CarpoolProgram carpoolProgram) {
		this.carpoolProgram = carpoolProgram;
	}

	public Pinche getInfo() {
		return info;
	}

	public void setInfo(Pinche info) {
		this.info = info;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getCarpoolProgramPassengerId() {
		return carpoolProgramPassengerId;
	}

	public int getCarpoolProgramId() {
		return carpoolProgramId;
	}

	public int getPassengerId() {
		return passengerId;
	}

	public long getAddTime() {
		return addTime;
	}

	public int getInfoId() {
		return infoId;
	}

	public int getCarpoolTimes() {
		return carpoolTimes;
	}

	public int getUnsettledTimes() {
		return unsettledTimes;
	}

	public String getState() {
		return state;
	}

	public String getDescrip() {
		return descrip;
	}

	public User getPassenger() {
		return passenger;
	}

	public int getProgramDate() {
		return programDate;
	}

	public void setCarpoolProgramPassengerId(int carpoolProgramPassengerId) {
		this.carpoolProgramPassengerId = carpoolProgramPassengerId;
	}

	public void setCarpoolProgramId(int carpoolProgramId) {
		this.carpoolProgramId = carpoolProgramId;
	}

	public void setPassengerId(int passengerId) {
		this.passengerId = passengerId;
	}

	public void setAddTime(long addTime) {
		this.addTime = addTime;
	}

	public void setInfoId(int infoId) {
		this.infoId = infoId;
	}

	public void setCarpoolTimes(int carpoolTimes) {
		this.carpoolTimes = carpoolTimes;
	}

	public void setUnsettledTimes(int unsettledTimes) {
		this.unsettledTimes = unsettledTimes;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setDescrip(String descrip) {
		this.descrip = descrip;
	}

	public void setPassenger(User passenger) {
		this.passenger = passenger;
	}

	public void setProgramDate(int programDate) {
		this.programDate = programDate;
	}

	@Override
	public String toString() {
		return "CarpoolProgramPassenger [carpoolProgramPassengerId=" + carpoolProgramPassengerId
				+ ", carpoolProgramId=" + carpoolProgramId + ", passengerId=" + passengerId
				+ ", addTime=" + addTime + ", infoId=" + infoId + ", carpoolTimes=" + carpoolTimes
				+ ", unsettledTimes=" + unsettledTimes + ", state=" + state + ", descrip="
				+ descrip + ", passenger=" + passenger + ", programDate=" + programDate + "]";
	}

}

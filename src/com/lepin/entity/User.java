package com.lepin.entity;

import java.io.Serializable;
import java.util.Arrays;

import android.content.Context;
import android.text.TextUtils;

import com.lepin.activity.R;

/**
 * 用户实体类，车辆默认为一辆
 * 
 */
public class User implements Serializable {
	public enum CHECK_STATE {
		CHECKED, CHECKING, NO_CHECK
	}

	public final static String GENDER_FEMALE = "FEMALE";
	public final static String GENDER_MALE = "MALE";
	public static final String LOCK_NORMAL = "LOCK_NORMAL";
	public static final String LOCK_PAYPWD = "LOCK_PAYPWD";// 支付密码错误5次锁定
	public static final String PUSH_CLOSED = "CLOSE";
	public static final String PUSH_OPEN = "OPEN";
	private static final long serialVersionUID = 1L;
	public static final String STATE_AUTH = "AUTH";// 用户状态：已经验证
	public static final String STATE_NORMAL = "NORMAL";// 用户状态：未验证

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	private String birthday;// 生日
	private Car car;
	private CHECK_STATE checkState = CHECK_STATE.NO_CHECK;
	private String driveAge;// 驾龄
	private String email;// 邮箱
	private int emailAuth;// 邮箱是否认证

	private String gender;// 性别

	private String imgPath;// 用户头像的完整http地址

	private int infoCountsAsDriver; // 用户作为司机发布的线路条数

	private int infoCountsAsPassenger; // 用户作为乘客发布的线路条数

	private boolean isCar;// 是否有车

	private String lockState;// 锁定状态，目前用于判断用户是否处于“密码输入错误超过5次”的锁定状态

	private MyAddr[] myAddrs;// 常用地址数组
	private boolean payPwdSet = false;// 是否设置支付密码

	private String pushSwitch;// 推送开关
	private String state;// 状态
	private String tel;// 电话
	private int telAuth;// 电话是否认证

	private int userId;// 用户id

	private String username;// 昵称

	public User() {
		super();
	}

	public String getBirthday() {
		return birthday;
	}

	public String getBirthday(Context mContext) {
		return (birthday == null || birthday.equals("")) ? mContext.getString(R.string.not_setting)
				: birthday;
	}

	public Car getCar() {
		return car == null ? null : car;
	}

	public CHECK_STATE getCheckState() {
		return checkState;
	}

	public String getDriveAge() {
		return driveAge;
	}

	public String getEmail() {
		return email;
	}

	public int getEmailAuth() {
		return emailAuth;
	}

	public String getGender() {
		return gender;
	}

	public String getGender(Context mContext) {

		if (TextUtils.isEmpty(gender) || gender.equals("null")) {
			return mContext.getString(R.string.not_setting);
		}

		return gender.equals(GENDER_MALE) ? mContext.getString(R.string.my_data_gender_m)
				: mContext.getString(R.string.my_data_gender_f);
	}

	public String getImgPath() {
		return imgPath;
	}

	public int getInfoCountsAsDriver() {
		return infoCountsAsDriver;
	};

	public int getInfoCountsAsPassenger() {
		return infoCountsAsPassenger;
	}

	public String getLockState() {
		return lockState;
	}

	public MyAddr[] getMyAddrs() {
		return myAddrs;
	}

	public String getPushSwitch() {
		return pushSwitch;
	}

	public String getState() {
		return state;
	}

	public String getTel() {
		return tel;
	}

	public int getTelAuth() {
		return telAuth;
	}

	public int getUserId() {
		return userId;
	}

	public String getUsername() {
		return username;
	}

	public String getUsername(Context mContext) {
		return (username == null || username.equals("")) ? mContext
				.getString(R.string.anonymous_user) : username;
	}

	public boolean isCar() {
		return isCar;
	}

	public boolean isPayPwdSet() {
		return payPwdSet;
	}

	/**
	 * 用户的状态是否验证
	 * 
	 * @return
	 */
	public boolean isUserStateVerify() {
		if (!TextUtils.isEmpty(state)) {
			return state.equals(STATE_AUTH) ? true : false;
		}
		return false;
	}

	public void setBirthday(String birthday) {
		if (!TextUtils.isEmpty(birthday)) {
			this.birthday = birthday;
		}
	}

	public void setCar(boolean isCar) {
		this.isCar = isCar;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	public void setCheckState(CHECK_STATE checkState) {
		this.checkState = checkState;
	}

	public void setDriveAge(String driveAge) {
		this.driveAge = driveAge;
	}

	public void setEmail(String email) {
		if (!TextUtils.isEmpty(email)) {
			this.email = email;
		}
	}

	public void setEmailAuth(int emailAuth) {
		this.emailAuth = emailAuth;
	}

	public void setGender(String gender) {
		if (!TextUtils.isEmpty(gender)) {
			this.gender = gender;
		}
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public void setInfoCountsAsDriver(int infoCountsAsDriver) {
		this.infoCountsAsDriver = infoCountsAsDriver;
	}

	public void setInfoCountsAsPassenger(int infoCountsAsPassenger) {
		this.infoCountsAsPassenger = infoCountsAsPassenger;
	}

	public void setLockState(String lockState) {
		this.lockState = lockState;
	}

	public void setMyAddrs(MyAddr[] myAddrs) {
		this.myAddrs = myAddrs;
	}

	public void setPayPwdSet(boolean payPwdSet) {
		this.payPwdSet = payPwdSet;
	}

	public void setPushSwitch(String pushSwitch) {
		this.pushSwitch = pushSwitch;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setTel(String tel) {
		if (!TextUtils.isEmpty(tel)) {
			this.tel = tel;
		}
	}

	public void setTelAuth(int telAuth) {
		this.telAuth = telAuth;
	}

	public void setUser_id(int userId) {
		this.userId = userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setUsername(String username) {
		if (!TextUtils.isEmpty(username)) {
			this.username = username;
		}
	}

	@Override
	public String toString() {
		return "User [birthday=" + birthday + ", car=" + car + ", checkState=" + checkState
				+ ", driveAge=" + driveAge + ", email=" + email + ", emailAuth=" + emailAuth
				+ ", gender=" + gender + ", imgPath=" + imgPath + ", infoCountsAsDriver="
				+ infoCountsAsDriver + ", infoCountsAsPassenger=" + infoCountsAsPassenger
				+ ", isCar=" + isCar + ", lockState=" + lockState + ", myAddrs="
				+ Arrays.toString(myAddrs) + ", payPwdSet=" + payPwdSet + ", pushSwitch="
				+ pushSwitch + ", state=" + state + ", tel=" + tel + ", telAuth=" + telAuth
				+ ", userId=" + userId + ", username=" + username + "]";
	}

}

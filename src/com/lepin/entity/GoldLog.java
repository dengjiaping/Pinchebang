package com.lepin.entity;

import java.io.Serializable;

import com.lepin.util.TimeUtils;

/**
 * 账户消费记录
 * 
 * @author zhiqiang
 * 
 */
public class GoldLog implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int goldLogId;// 提现账户编号
	private int userId;
	private String amount;// 金额
	private String balance;// 操作后的余额
	private String brief;// 备注
	private String logType;// 日志类型

	public final static String LOGTYPE_RECHARGE = "RECHARGE";
	public final static String LOGTYPE_WITHDRAW_CASH = "WITHDRAW_CASH";
	private long logTime;

	public int getGoldLogId() {
		return goldLogId;
	}

	public int getUserId() {
		return userId;
	}

	public String getAmount() {
		return amount;
	}

	public String getBalance() {
		return balance;
	}

	public String getBrief() {
		return brief;
	}

	public String getLogType() {
		return logType;
	}

	public String getLogTime() {
		if (logTime > 0) {

			return TimeUtils.formatDate(logTime, TimeUtils.TIME_FORMART_HMS);
		} else {
			return "";
		}
	}

	public void setGoldLogId(int goldLogId) {
		this.goldLogId = goldLogId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public void setLogTime(long logTime) {
		this.logTime = logTime;
	}

	@Override
	public String toString() {
		return "GoldLog [goldLogId=" + goldLogId + ", userId=" + userId + ", amount=" + amount
				+ ", balance=" + balance + ", brief=" + brief + ", logType=" + logType
				+ ", logTime=" + logTime + "]";
	}

}

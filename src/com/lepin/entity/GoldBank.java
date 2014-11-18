package com.lepin.entity;

import java.io.Serializable;

/**
 * 提现账户
 * 
 * @author zhiqiang
 * 
 */
public class GoldBank implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int goldBankId;// 提现账户编号
	private int userId;
	private String bankType;// 帐号类型：目前只有支付宝
	private String account;// 账户
	private String trueName;// 账户姓名

	public int getGoldBankId() {
		return goldBankId;
	}

	public int getUserId() {
		return userId;
	}

	public String getBankType() {
		return bankType;
	}

	public String getAccount() {
		return account;
	}

	public String getTrueName() {
		return trueName;
	}

	public void setGoldBankId(int goldBankId) {
		this.goldBankId = goldBankId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setBankType(String bankType) {
		this.bankType = bankType;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setTrueName(String trueName) {
		this.trueName = trueName;
	}

	@Override
	public String toString() {
		return "GoldBank [goldBankId=" + goldBankId + ", userId=" + userId + ", bankType="
				+ bankType + ", account=" + account + ", trueName=" + trueName + "]";
	}

}

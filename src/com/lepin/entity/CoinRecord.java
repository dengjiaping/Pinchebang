package com.lepin.entity;

public class CoinRecord {

	private String amount;// 金额
	private String brief;// 消费、充值类型
	private String logTime;// 消费时间

	public CoinRecord(String brief, String logTime, String amount) {
		this.amount = amount;
		this.brief = brief;
		this.logTime = logTime;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getLogTime() {
		return logTime;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	@Override
	public String toString() {
		return "CoinRecord [amount=" + amount + ", brief=" + brief + ", log_time=" + logTime + "]";
	}

}

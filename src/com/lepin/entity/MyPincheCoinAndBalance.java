package com.lepin.entity;

import java.io.Serializable;

public class MyPincheCoinAndBalance implements Serializable {

	private String coin;// 拼车币
	private String gold;// 余额

	public String getCoin() {
		return coin;
	}

	public void setCoin(String coin) {
		this.coin = coin;
	}

	public String getGold() {
		return gold;
	}

	public void setGold(String gold) {
		this.gold = gold;
	}

	@Override
	public String toString() {
		return "Coin & balance [coin=" + coin + ", gold=" + gold + "]";
	}

}

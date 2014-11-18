package com.lepin.entity;

import java.io.Serializable;

public class CycleDate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String txt;
	private String nums;

	public String getTxt() {
		return txt;
	}

	public String getNums() {
		return nums;
	}

	public void setTxt(String txt) {
		this.txt = txt;
	}

	public void setNums(String nums) {
		this.nums = nums;
	}

	@Override
	public String toString() {
		return "CycleDate [txt=" + txt + ", nums=" + nums + "]";
	}

}

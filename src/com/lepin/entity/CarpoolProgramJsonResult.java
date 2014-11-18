package com.lepin.entity;

import java.io.Serializable;

public class CarpoolProgramJsonResult implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private CarpoolProgram driver;

	private CarpoolProgramPassenger passenger;

	public CarpoolProgram getDriver() {
		return driver;
	}

	public CarpoolProgramPassenger getPassenger() {
		return passenger;
	}

	public void setDriver(CarpoolProgram driver) {
		this.driver = driver;
	}

	public void setPassenger(CarpoolProgramPassenger passenger) {
		this.passenger = passenger;
	}

	@Override
	public String toString() {
		return "CarpoolProgramJsonResult [driver=" + driver + ", passenger=" + passenger + "]";
	}
	
}

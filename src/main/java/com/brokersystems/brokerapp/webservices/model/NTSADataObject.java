
package com.brokersystems.brokerapp.webservices.model;

import java.util.List;

public class NTSADataObject {
	
	  private String chassisNumber;
	  private String regNo;
	  private List<VehicleOwner> owner;
	  private String Details;
	  private String caveat;
	  private VehicleDetails vehicle;
	public String getChassisNumber() {
		return chassisNumber;
	}
	public void setChassisNumber(String chassisNumber) {
		this.chassisNumber = chassisNumber;
	}
	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	public List<VehicleOwner> getOwner() {
		return owner;
	}
	public void setOwner(List<VehicleOwner> owner) {
		this.owner = owner;
	}
	public String getDetails() {
		return Details;
	}
	public void setDetails(String details) {
		Details = details;
	}
	public String getCaveat() {
		return caveat;
	}
	public void setCaveat(String caveat) {
		this.caveat = caveat;
	}
	public VehicleDetails getVehicle() {
		return vehicle;
	}
	public void setVehicle(VehicleDetails vehicle) {
		this.vehicle = vehicle;
	}

	  
	  
	
 
}

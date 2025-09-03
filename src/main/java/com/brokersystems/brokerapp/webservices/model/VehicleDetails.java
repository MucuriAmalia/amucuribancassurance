
package com.brokersystems.brokerapp.webservices.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class VehicleDetails {
	
	private String regNo; 
	@JsonProperty(value = "ChassisNo")
	private String ChassisNo;
	private String yearOfManufacture; 
	private String carModel;
	private String carMake; 
	private String bodyType; 
	private String logbookNumber;
	private String registrationDate; 
	private String bodyColor; 
	private String dutyStatus;
	private String dutyAmount; 
	private String dutyDate; 
	private String engineNumber;
	private BigDecimal carryCapacity;
	private BigDecimal engineCapacity;
	private BigDecimal mileage;
	private Long riskId;
	private Long vdId;

	public Long getVdId() {
		return vdId;
	}

	public void setVdId(Long vdId) {
		this.vdId = vdId;
	}

	public Long getRiskId() {
		return riskId;
	}

	public void setRiskId(Long riskId) {
		this.riskId = riskId;
	}

	public BigDecimal getCarryCapacity() {
		return carryCapacity;
	}

	public void setCarryCapacity(BigDecimal carryCapacity) {
		this.carryCapacity = carryCapacity;
	}

	public BigDecimal getEngineCapacity() {
		return engineCapacity;
	}

	public void setEngineCapacity(BigDecimal engineCapacity) {
		this.engineCapacity = engineCapacity;
	}

	public BigDecimal getMileage() {
		return mileage;
	}

	public void setMileage(BigDecimal mileage) {
		this.mileage = mileage;
	}

	public String getRegNo() {
		return regNo;
	}
	public void setRegNo(String regNo) {
		this.regNo = regNo;
	}
	@JsonProperty(value = "ChassisNo")
	public String getChassisNo() {
		return ChassisNo;
	}
	public void setChassisNo(String chassisNo) {
		ChassisNo = chassisNo;
	}
	public String getYearOfManufacture() {
		return yearOfManufacture;
	}
	public void setYearOfManufacture(String yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}
	public String getCarModel() {
		return carModel;
	}
	public void setCarModel(String carModel) {
		this.carModel = carModel;
	}
	public String getCarMake() {
		return carMake;
	}
	public void setCarMake(String carMake) {
		this.carMake = carMake;
	}
	public String getBodyType() {
		return bodyType;
	}
	public void setBodyType(String bodyType) {
		this.bodyType = bodyType;
	}
	public String getLogbookNumber() {
		return logbookNumber;
	}
	public void setLogbookNumber(String logbookNumber) {
		this.logbookNumber = logbookNumber;
	}
	public String getRegistrationDate() {
		return registrationDate;
	}
	public void setRegistrationDate(String registrationDate) {
		this.registrationDate = registrationDate;
	}
	public String getBodyColor() {
		return bodyColor;
	}
	public void setBodyColor(String bodyColor) {
		this.bodyColor = bodyColor;
	}
	public String getDutyStatus() {
		return dutyStatus;
	}
	public void setDutyStatus(String dutyStatus) {
		this.dutyStatus = dutyStatus;
	}
	public String getDutyAmount() {
		return dutyAmount;
	}
	public void setDutyAmount(String dutyAmount) {
		this.dutyAmount = dutyAmount;
	}
	public String getDutyDate() {
		return dutyDate;
	}
	public void setDutyDate(String dutyDate) {
		this.dutyDate = dutyDate;
	}
	public String getEngineNumber() {
		return engineNumber;
	}
	public void setEngineNumber(String engineNumber) {
		this.engineNumber = engineNumber;
	}
	
	

}

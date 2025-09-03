package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;

public class SectionBean {
	
    private Long Id;
	
	private String shtDesc;
	
	private String desc;
	
	private BigDecimal rate;
	
	private BigDecimal freeLimit;
	
	private BigDecimal divFactor;
	
	private String rateType;
	
	private String sectType;
	
	private Long premId;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public String getShtDesc() {
		return shtDesc;
	}

	public void setShtDesc(String shtDesc) {
		this.shtDesc = shtDesc;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(BigDecimal freeLimit) {
		this.freeLimit = freeLimit;
	}

	public BigDecimal getDivFactor() {
		return divFactor;
	}

	public void setDivFactor(BigDecimal divFactor) {
		this.divFactor = divFactor;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getSectType() {
		return sectType;
	}

	public void setSectType(String sectType) {
		this.sectType = sectType;
	}

	public Long getPremId() {
		return premId;
	}

	public void setPremId(Long premId) {
		this.premId = premId;
	}
	
	

}

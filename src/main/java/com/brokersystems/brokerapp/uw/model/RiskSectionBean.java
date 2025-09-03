package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public class RiskSectionBean {
	
	private BigInteger sectId;

	private Long pk;
	
	private Long section;
	
	private BigDecimal amount;
	
	private BigDecimal rate;
	
	private BigDecimal divFactor;
	
	private BigDecimal freeLimit;
	
	private boolean compute;
	
	private Long premId;

	private String sectionDesc;
	
	private BigDecimal multiplierRate;

	private String rangeType;

	private String ratesApplicable;

	private String mandatory;

	private String rider;

	private String limitsAllowed;


	public Long getPk() {
		return pk;
	}

	public void setPk(Long pk) {
		this.pk = pk;
	}

	public String getLimitsAllowed() {
		return limitsAllowed;
	}

	public void setLimitsAllowed(String limitsAllowed) {
		this.limitsAllowed = limitsAllowed;
	}

	public BigInteger getSectId() {
		return sectId;
	}

	public void setSectId(BigInteger sectId) {
		this.sectId = sectId;
	}

	public Long getSection() {
		return section;
	}

	public void setSection(Long section) {
		this.section = section;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getDivFactor() {
		return divFactor;
	}

	public void setDivFactor(BigDecimal divFactor) {
		this.divFactor = divFactor;
	}

	public BigDecimal getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(BigDecimal freeLimit) {
		this.freeLimit = freeLimit;
	}

	public boolean isCompute() {
		return compute;
	}

	public void setCompute(boolean compute) {
		this.compute = compute;
	}

	public Long getPremId() {
		return premId;
	}

	public void setPremId(Long premId) {
		this.premId = premId;
	}

	public BigDecimal getMultiplierRate() {
		return multiplierRate;
	}

	public void setMultiplierRate(BigDecimal multiplierRate) {
		this.multiplierRate = multiplierRate;
	}

	public String getSectionDesc() {
		return sectionDesc;
	}

	public void setSectionDesc(String sectionDesc) {
		this.sectionDesc = sectionDesc;
	}

	public String getRatesApplicable() {
		return ratesApplicable;
	}

	public void setRatesApplicable(String ratesApplicable) {
		this.ratesApplicable = ratesApplicable;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getRider() {
		return rider;
	}

	public void setRider(String rider) {
		this.rider = rider;
	}


	public String getRangeType() {
		return rangeType;
	}

	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}

	@Override
	public String toString() {
		return "RiskSectionBean{" +
				"sectId=" + sectId +
				", section=" + section +
				", amount=" + amount +
				", rate=" + rate +
				", divFactor=" + divFactor +
				", freeLimit=" + freeLimit +
				", compute=" + compute +
				", premId=" + premId +
				", sectionDesc='" + sectionDesc + '\'' +
				", multiplierRate=" + multiplierRate +
				", ratesApplicable='" + ratesApplicable + '\'' +
				", mandatory='" + mandatory + '\'' +
				", rider='" + rider + '\'' +
				", rangeType='" + rangeType + '\'' +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		RiskSectionBean that = (RiskSectionBean) o;

		return premId.equals(that.premId);

	}

	@Override
	public int hashCode() {
		return (premId != null) ? premId.hashCode() : 0;
	}
}

package com.brokersystems.brokerapp.server.utils;

import java.math.BigDecimal;

public class SectionImportBean {
	
	private String riskId;
	private   String sectionId;
	private   BigDecimal limit;
	private   double rate;
	private double divFactor;
	
	
	public String getRiskId() {
		return riskId;
	}
	public void setRiskId(String riskId) {
		this.riskId = riskId;
	}

	public String getSectionId() {
		return sectionId;
	}
	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}
	public BigDecimal getLimit() {
		return limit;
	}
	public void setLimit(BigDecimal limit) {
		this.limit = limit;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}

	public double getDivFactor() {
		return divFactor;
	}

	public void setDivFactor(double divFactor) {
		this.divFactor = divFactor;
	}

	@Override
	public String toString() {
		return "SectionImportBean [riskId=" + riskId + ", sectionId=" + sectionId + ", limit=" + limit + ", rate="
				+ rate + "]";
	}
	
	
	
	

}

package com.brokersystems.brokerapp.server.utils;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@ToString
public class PremiumItemsBean {
	
	private final String premiumId;
	private final double rate;
	private final double freeLimit;
	private final double minPrem;
	private final double value;
	private final double divFactor;
	private final Long sectId;
    private final String sectType;
	private final List<String> scheduleItems;
	private final Integer order;
	private BigDecimal sumAssured;
	private BigDecimal premium;
	private String frequency;
	private int term;
	private int age;
	private boolean mainSection;
	private Long polId;
	private BigDecimal investment;
	private BigDecimal topUp;
	private String investmentFreq;
	private String topUpFreq;
	
	
	public PremiumItemsBean(String premiumId, double rate, double freeLimit, double minPrem, double value,Long sectId,double divFactor,String sectType,Integer order,List<String> scheduleItems, Long polId) {
		this.premiumId = premiumId;
		this.rate = rate;
		this.freeLimit = freeLimit;
		this.minPrem = minPrem;
		this.value = value;
		this.sectId = sectId;
		this.divFactor = divFactor;
		this.sectType = sectType;
		this.scheduleItems = scheduleItems;
		this.order = order;
		this.polId = polId;
	}

	public String getTopUpFreq() {
		return topUpFreq;
	}

	public void setTopUpFreq(String topUpFreq) {
		this.topUpFreq = topUpFreq;
	}

	public String getInvestmentFreq() {
		return investmentFreq;
	}

	public void setInvestmentFreq(String investmentFreq) {
		this.investmentFreq = investmentFreq;
	}

	public boolean isMainSection() {
		return mainSection;
	}

	public void setMainSection(boolean mainSection) {
		this.mainSection = mainSection;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public int getTerm() {
		return term;
	}

	public void setTerm(int term) {
		this.term = term;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public BigDecimal getSumAssured() {
		return sumAssured;
	}

	public void setSumAssured(BigDecimal sumAssured) {
		this.sumAssured = sumAssured;
	}

	public BigDecimal getPremium() {
		return premium;
	}

	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}

	public double getValue() {
		return value;
	}
	public String getPremiumId() {
		return premiumId;
	}
	public double getRate() {
		return rate;
	}
	public double getFreeLimit() {
		return freeLimit;
	}
	public double getMinPrem() {
		return minPrem;
	}

	public Long getSectId() {
		return sectId;
	}

	public double getDivFactor() {
		return divFactor;
	}

	public Long getPolId() {
		return polId;
	}

	public void setPolId(Long polId) {
		this.polId = polId;
	}

	public String getSectType() {
		return sectType;
	}

	public List<String> getScheduleItems() {
		return scheduleItems;
	}

	public Integer getOrder() {
		return order;
	}

	public BigDecimal getInvestment() {
		return investment;
	}

	public void setInvestment(BigDecimal investment) {
		this.investment = investment;
	}

	public BigDecimal getTopUp() {
		return topUp;
	}

	public void setTopUp(BigDecimal topUp) {
		this.topUp = topUp;
	}

	@Override
	public String toString() {
		return "PremiumItemsBean{" +
				"premiumId='" + premiumId + '\'' +
				", rate=" + rate +
				", freeLimit=" + freeLimit +
				", minPrem=" + minPrem +
				", value=" + value +
				", divFactor=" + divFactor +
				", sectId=" + sectId +
				", sectType='" + sectType + '\'' +
				", scheduleItems=" + scheduleItems +
				", order=" + order +
				", sumAssured=" + sumAssured +
				", premium=" + premium +
				", frequency='" + frequency + '\'' +
				", term=" + term +
				", age=" + age +
				", mainSection=" + mainSection +
				", polId=" + polId +
				", investment=" + investment +
				", topUp=" + topUp +
				", investmentFreq=" + investmentFreq +
				", topUpFreq=" + topUpFreq +
				'}';
	}
}

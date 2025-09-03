package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

public class RiskTransBean {

	private BigInteger riskId;

	private String riskShtDesc;

	private String riskDesc;

	private Long insuredCode;

	private Integer workingAge;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	private Date wefDate;

	@JsonFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	private Date wetDate;

	private Long bindCode;

	private Long sclCode;

	private Long coverCode;

	private BigDecimal commRate;

	private BigDecimal subAgentCommRate;

	private BigDecimal introducerCommRate;

	private BigDecimal marketerCommRate;

	private String prorata;
	
	private Long binderDet;

	private String autogenCert;

	private BigDecimal premium;

	private BigDecimal sumInsured;

	private BigDecimal investment;

	private BigDecimal topUp;

	private String computeType;

	private BigDecimal butchargePrem;

	private Long installmentNo;

	private String installmentPerc;

	private BigDecimal installAmount;

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

	public Long getInstallmentNo() {
		return installmentNo;
	}

	public void setInstallmentNo(Long installmentNo) {
		this.installmentNo = installmentNo;
	}

	public String getInstallmentPerc() {
		return installmentPerc;
	}

	public void setInstallmentPerc(String installmentPerc) {
		this.installmentPerc = installmentPerc;
	}

	public BigDecimal getInstallAmount() {
		return installAmount;
	}

	public void setInstallAmount(BigDecimal installAmount) {
		this.installAmount = installAmount;
	}

	public BigInteger getRiskId() {
		return riskId;
	}

	public void setRiskId(BigInteger riskId) {
		this.riskId = riskId;
	}

	public String getRiskShtDesc() {
		return riskShtDesc;
	}

	public void setRiskShtDesc(String riskShtDesc) {
		this.riskShtDesc = riskShtDesc;
	}

	public String getRiskDesc() {
		return riskDesc;
	}

	public void setRiskDesc(String riskDesc) {
		this.riskDesc = riskDesc;
	}


	public Date getWefDate() {
		return wefDate;
	}

	public void setWefDate(Date wefDate) {
		this.wefDate = wefDate;
	}

	public Date getWetDate() {
		return wetDate;
	}

	public void setWetDate(Date wetDate) {
		this.wetDate = wetDate;
	}

	public Integer getWorkingAge() {
		return workingAge;
	}

	public void setWorkingAge(Integer workingAge) {
		this.workingAge = workingAge;
	}

	public BigDecimal getCommRate() {
		return commRate;
	}

	public void setCommRate(BigDecimal commRate) {
		this.commRate = commRate;
	}

	public String getProrata() {
		return prorata;
	}

	public void setProrata(String prorata) {
		this.prorata = prorata;
	}

	public Long getBinderDet() {
		return binderDet;
	}

	public void setBinderDet(Long binderDet) {
		this.binderDet = binderDet;
	}

	public Long getInsuredCode() {
		return insuredCode;
	}

	public void setInsuredCode(Long insuredCode) {
		this.insuredCode = insuredCode;
	}

	public Long getBindCode() {
		return bindCode;
	}

	public void setBindCode(Long bindCode) {
		this.bindCode = bindCode;
	}

	public Long getSclCode() {
		return sclCode;
	}

	public void setSclCode(Long sclCode) {
		this.sclCode = sclCode;
	}

	public Long getCoverCode() {
		return coverCode;
	}

	public void setCoverCode(Long coverCode) {
		this.coverCode = coverCode;
	}

	public String getAutogenCert() {
		return autogenCert;
	}

	public void setAutogenCert(String autogenCert) {
		this.autogenCert = autogenCert;
	}

	public BigDecimal getPremium() {
		return premium;
	}

	public void setPremium(BigDecimal premium) {
		this.premium = premium;
	}

	public BigDecimal getSumInsured() {
		return sumInsured;
	}

	public void setSumInsured(BigDecimal sumInsured) {
		this.sumInsured = sumInsured;
	}

	public String getComputeType() {
		return computeType;
	}

	public void setComputeType(String computeType) {
		this.computeType = computeType;
	}

	public BigDecimal getButchargePrem() {
		return butchargePrem;
	}

	public void setButchargePrem(BigDecimal butchargePrem) {
		this.butchargePrem = butchargePrem;
	}

	public BigDecimal getSubAgentCommRate() {
		return subAgentCommRate;
	}

	public void setSubAgentCommRate(BigDecimal subAgentCommRate) {
		this.subAgentCommRate = subAgentCommRate;
	}

	public BigDecimal getIntroducerCommRate() {
		return introducerCommRate;
	}

	public void setIntroducerCommRate(BigDecimal introducerCommRate) {
		this.introducerCommRate = introducerCommRate;
	}

	public BigDecimal getMarketerCommRate() {
		return marketerCommRate;
	}

	public void setMarketerCommRate(BigDecimal marketerCommRate) {
		this.marketerCommRate = marketerCommRate;
	}
}

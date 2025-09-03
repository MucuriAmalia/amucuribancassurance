package com.brokersystems.brokerapp.setup.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the Section Short Period rates database table.
 * 
 */
@Entity
@Table(name="sys_sec_short_prd_rates")
public class SecShortPeriodRates {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="ssp_code")
	private Long spCode;
	
	@Column(name="ssp_active")
	private boolean active;
	
	@Column(name="ssp_prd_from",nullable=false)
	private Long periodFrom;
	
	@Column(name="ssp_prd_to",nullable=false)
	private Long periodTo;
	
	@Column(name="ssp_prd_rate",nullable=false)
	private BigDecimal rate;
	
	
	@Column(name="ssp_prd_div_fact",nullable=false)
	private BigDecimal divFactor;
	
	@ManyToOne
	@JoinColumn(name="ssp_subsec_code",nullable=false)
	private SubclassSections section;

	public Long getSpCode() {
		return spCode;
	}

	public void setSpCode(Long spCode) {
		this.spCode = spCode;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Long getPeriodFrom() {
		return periodFrom;
	}

	public void setPeriodFrom(Long periodFrom) {
		this.periodFrom = periodFrom;
	}

	public Long getPeriodTo() {
		return periodTo;
	}

	public void setPeriodTo(Long periodTo) {
		this.periodTo = periodTo;
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

	public SubclassSections getSection() {
		return section;
	}

	public void setSection(SubclassSections section) {
		this.section = section;
	}
	
	
	

}

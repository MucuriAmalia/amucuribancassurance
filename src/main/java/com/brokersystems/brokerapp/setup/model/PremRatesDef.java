package com.brokersystems.brokerapp.setup.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.brokersystems.brokerapp.quotes.model.QuotRiskLimits;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="sys_brk_prem_rts")
public class PremRatesDef extends AuditBaseEntity{
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="prem_id")
	private Long Id;
	
	@ManyToOne
	@JoinColumn(name="prem_bd_code",nullable=false)
	private BinderDetails binderDet;
	
	@ManyToOne
	@JoinColumn(name="prem_sec_code",nullable=false)
	private SectionsDef section;
	
	@Column(name="prem_status",nullable=false)
	private boolean active;
	
	@Column(name="prem_rate",nullable=false)
	private BigDecimal rate;
	
	@Column(name="prem_min_rate")
	private BigDecimal minRate;
	
	@Column(name="prem_max_rate")
	private BigDecimal maxrate;
	
	@Column(name="prem_range_from")
	private BigDecimal rangeFrom;
	
	@Column(name="prem_range_to")
	private BigDecimal rangeTo;
	
	@Column(name="prem_rate_type")
	private String rateType;

	@Column(name="prem_range_type")
	private String rangeType;


	@Column(name="prem_div_fact")
	private BigDecimal divFactor;
	
	@Column(name="prem_multi_rate")
	private BigDecimal multiRate;
	
	@Column(name="prem_multi_div_fact")
	private BigDecimal multiDivFactor;
	
	@Column(name="prem_prorate_full",nullable=false)
	private String proratedFull;
	
	@Column(name="prem_free_limit")
	private BigDecimal freeLimit;
	
	@Column(name="prem_min_prem")
	private BigDecimal minPremium;

	@Column(name="prem_mandatory",length = 1)
	private String mandatory;
	
	@ManyToOne
	@JoinColumn(name="scs_scs_code")
	private SubCoverTypeSections subSections;
	
	@JsonIgnore
	@OneToMany(mappedBy="premRates")
	private List<SectionTrans> sectionTrans;

	@JsonIgnore
	@OneToMany(mappedBy="premRates")
	private List<QuotRiskLimits> quotRiskLimits;

	@Column(name="prem_rates_appl")
	private String rangeApplicable;

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
	}

	public BinderDetails getBinderDet() {
		return binderDet;
	}

	public void setBinderDet(BinderDetails binderDet) {
		this.binderDet = binderDet;
	}

	public SectionsDef getSection() {
		return section;
	}

	public void setSection(SectionsDef section) {
		this.section = section;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public BigDecimal getRangeFrom() {
		return rangeFrom;
	}

	public void setRangeFrom(BigDecimal rangeFrom) {
		this.rangeFrom = rangeFrom;
	}

	public BigDecimal getRangeTo() {
		return rangeTo;
	}

	public void setRangeTo(BigDecimal rangeTo) {
		this.rangeTo = rangeTo;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public BigDecimal getDivFactor() {
		return divFactor;
	}

	public void setDivFactor(BigDecimal divFactor) {
		this.divFactor = divFactor;
	}

	public String getProratedFull() {
		return proratedFull;
	}

	public void setProratedFull(String proratedFull) {
		this.proratedFull = proratedFull;
	}

	public BigDecimal getFreeLimit() {
		return freeLimit;
	}

	public void setFreeLimit(BigDecimal freeLimit) {
		this.freeLimit = freeLimit;
	}

	public BigDecimal getMinRate() {
		return minRate;
	}

	public void setMinRate(BigDecimal minRate) {
		this.minRate = minRate;
	}

	public BigDecimal getMaxrate() {
		return maxrate;
	}

	public void setMaxrate(BigDecimal maxrate) {
		this.maxrate = maxrate;
	}

	public BigDecimal getMultiRate() {
		return multiRate;
	}

	public void setMultiRate(BigDecimal multiRate) {
		this.multiRate = multiRate;
	}

	public BigDecimal getMultiDivFactor() {
		return multiDivFactor;
	}

	public void setMultiDivFactor(BigDecimal multiDivFactor) {
		this.multiDivFactor = multiDivFactor;
	}



	public BigDecimal getMinPremium() {
		return minPremium;
	}

	public void setMinPremium(BigDecimal minPremium) {
		this.minPremium = minPremium;
	}



	public SubCoverTypeSections getSubSections() {
		return subSections;
	}

	public void setSubSections(SubCoverTypeSections subSections) {
		this.subSections = subSections;
	}

	public List<SectionTrans> getSectionTrans() {
		return sectionTrans;
	}

	public void setSectionTrans(List<SectionTrans> sectionTrans) {
		this.sectionTrans = sectionTrans;
	}


	public List<QuotRiskLimits> getQuotRiskLimits() {
		return quotRiskLimits;
	}

	public void setQuotRiskLimits(List<QuotRiskLimits> quotRiskLimits) {
		this.quotRiskLimits = quotRiskLimits;
	}

	public String getRangeApplicable() {
		return rangeApplicable;
	}

	public void setRangeApplicable(String ratesApplicable) {
		this.rangeApplicable = ratesApplicable;
	}

	public String getMandatory() {
		return mandatory;
	}

	public void setMandatory(String mandatory) {
		this.mandatory = mandatory;
	}

	public String getRangeType() {
		return rangeType;
	}

	public void setRangeType(String rangeType) {
		this.rangeType = rangeType;
	}
}

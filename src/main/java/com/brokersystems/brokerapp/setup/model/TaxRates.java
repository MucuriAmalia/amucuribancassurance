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
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "sys_brk_tax_rates")
public class TaxRates {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "tax_id")
	private Long taxId;
	
	@Column(name = "tax_rate",nullable=false)
	private BigDecimal taxRate;
	
	@Column(name = "tax_active")
	private boolean active;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "tax_sub_id",nullable=false)
	private SubClassDef subclass;

	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "tax_pro_id",nullable=false)
	private ProductsDef productsDef;
	
	@Column(name = "tax_range_from",nullable=false)
	private BigDecimal rangeFrom;
	
	@Column(name = "tax_range_to",nullable=false)
	private BigDecimal rangeTo;
	
	@Column(name = "tax_rate_desc")
	private String rateDesc;
	
	@Column(name = "tax_div_factor",nullable=false)
	private BigDecimal divFactor;
	
	@Column(name = "tax_rate_type",nullable=false)
	private String rateType;

	@Column(name = "tax_based_on")
	private String basedOn;
	
	@Column(name = "tax_level",nullable=false)
	private String taxLevel;

	@Column(name = "tax_mandatory")
	private Boolean mandatory;

	@ManyToOne
	@JoinColumn(name="tax_rev_code")
	private RevenueItemsDef revenueItems;

	public Long getTaxId() {
		return taxId;
	}

	public void setTaxId(Long taxId) {
		this.taxId = taxId;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
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

	public String getRateDesc() {
		return rateDesc;
	}

	public void setRateDesc(String rateDesc) {
		this.rateDesc = rateDesc;
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

	public String getBasedOn() {
		return basedOn;
	}

	public void setBasedOn(String basedOn) {
		this.basedOn = basedOn;
	}

	public String getTaxLevel() {
		return taxLevel;
	}

	public void setTaxLevel(String taxLevel) {
		this.taxLevel = taxLevel;
	}

	public RevenueItemsDef getRevenueItems() {
		return revenueItems;
	}

	public void setRevenueItems(RevenueItemsDef revenueItems) {
		this.revenueItems = revenueItems;
	}

	public Boolean getMandatory() {
		return mandatory;
	}

	public void setMandatory(Boolean mandatory) {
		this.mandatory = mandatory;
	}

	public ProductsDef getProductsDef() {
		return productsDef;
	}

	public void setProductsDef(ProductsDef productsDef) {
		this.productsDef = productsDef;
	}

	@Override
	public String toString() {
		return "TaxRates{" +
				"taxId=" + taxId +
				", revenueItems=" + revenueItems +
				'}';
	}
}

package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;
import com.brokersystems.brokerapp.setup.model.SubClassDef;

@Entity
@Table(name="sys_brk_pol_taxes")
public class PolicyTaxes {
	
	@Id
	@SequenceGenerator(name = "myPolTaxesSeq",sequenceName = "pol_taxes_seq",allocationSize=1)
	@GeneratedValue(generator = "myPolTaxesSeq")
	@Column(name="pol_tax_id")
	private Long polTaxId;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name="risk_pol_id")
	private PolicyTrans policy;
	
	@ManyToOne
	@JoinColumn(name="pol_tax_rev_code",nullable=false)
	private RevenueItemsDef revenueItems;
	
	@XmlTransient
	@ManyToOne
	@JoinColumn(name = "pol_tax_sub_id",nullable=false)
	private SubClassDef subclass;
	
	@Column(name="pol_tax_amount")
	private BigDecimal taxAmount;
	
	@Column(name = "pol_tax_rate",nullable=false)
	private BigDecimal taxRate;
	
	@Column(name = "pol_tax_div_fact",nullable=false)
	private BigDecimal divFactor;
	
	@Column(name = "pol_tax_rate_type",nullable=false)
	private String rateType;
	
	@Column(name = "pol_tax_level")
	private String taxLevel;

	public Long getPolTaxId() {
		return polTaxId;
	}

	public void setPolTaxId(Long polTaxId) {
		this.polTaxId = polTaxId;
	}

	public RevenueItemsDef getRevenueItems() {
		return revenueItems;
	}

	public void setRevenueItems(RevenueItemsDef revenueItems) {
		this.revenueItems = revenueItems;
	}

	public SubClassDef getSubclass() {
		return subclass;
	}

	public void setSubclass(SubClassDef subclass) {
		this.subclass = subclass;
	}

	public BigDecimal getTaxAmount() {
		return taxAmount;
	}

	public void setTaxAmount(BigDecimal taxAmount) {
		this.taxAmount = taxAmount;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
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

	public PolicyTrans getPolicy() {
		return policy;
	}

	public void setPolicy(PolicyTrans policy) {
		this.policy = policy;
	}

	public String getTaxLevel() {
		return taxLevel;
	}

	public void setTaxLevel(String taxLevel) {
		this.taxLevel = taxLevel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((policy == null) ? 0 : policy.hashCode());
		result = prime * result + ((revenueItems == null) ? 0 : revenueItems.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PolicyTaxes other = (PolicyTaxes) obj;
		if (policy == null) {
			if (other.policy != null)
				return false;
		} else if (!policy.equals(other.policy))
			return false;
		if (revenueItems == null) {
			if (other.revenueItems != null)
				return false;
		} else if (!revenueItems.equals(other.revenueItems))
			return false;
		return true;
	}
	
	
	
	

}

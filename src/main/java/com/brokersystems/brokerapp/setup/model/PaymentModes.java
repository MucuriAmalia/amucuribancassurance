package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.accounts.model.CoaSubAccounts;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name="sys_brk_payment_modes")
public class PaymentModes extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="pm_id")
	private Long pmId;
	
	@Column(name="pm_sh_desc",nullable=false,unique=true)
	private String pmShtDesc;
	
	@Column(name="pm_desc")
	private String pmDesc;
	
	@Column(name="pm_max_val")
	private BigDecimal pmMaxValue;
	
	@Column(name="pm_min_val")
	private BigDecimal pmMinValue;

	@Column(name = "pm_supports_check",length = 1)
	private String supportsCheque;

	@XmlTransient
	@JsonIgnore
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="pm_co_id")
	private CoaSubAccounts subAccounts;

	public String getSupportsCheque() {
		return supportsCheque;
	}

	public void setSupportsCheque(String supportsCheque) {
		this.supportsCheque = supportsCheque;
	}

	public Long getPmId() {
		return pmId;
	}

	public void setPmId(Long pmId) {
		this.pmId = pmId;
	}

	public String getPmShtDesc() {
		return pmShtDesc;
	}

	public void setPmShtDesc(String pmShtDesc) {
		this.pmShtDesc = pmShtDesc;
	}

	public String getPmDesc() {
		return pmDesc;
	}

	public void setPmDesc(String pmDesc) {
		this.pmDesc = pmDesc;
	}

	public BigDecimal getPmMaxValue() {
		return pmMaxValue;
	}

	public void setPmMaxValue(BigDecimal pmMaxValue) {
		this.pmMaxValue = pmMaxValue;
	}

	public BigDecimal getPmMinValue() {
		return pmMinValue;
	}

	public void setPmMinValue(BigDecimal pmMinValue) {
		this.pmMinValue = pmMinValue;
	}


	public CoaSubAccounts getSubAccounts() {
		return subAccounts;
	}

	public void setSubAccounts(CoaSubAccounts subAccounts) {
		this.subAccounts = subAccounts;
	}
}

package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import lombok.ToString;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_account_types")
@ToString
public class AccountTypes extends AuditBaseEntity {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="acc_id")
	private Long accId;
	
	@Column(name="acc_sht_desc",nullable=false,unique=true)
	private String accShtDesc;
	
	@Column(name="acc_name")
	private String accName;
	
	@Column(name="acc_vat_appl")
	private boolean vatAppli;
	
	@Column(name="acc_vat_rate")
	private BigDecimal vatRate;
	
	@Column(name="acc_whtx_appl")
	private boolean whtxAppl;
	
	@Column(name="acc_whtx_rate")
	private BigDecimal whtaxVal;
	
	@Column(name="acc_comm_rate")
	private BigDecimal commRate;

	@Column(name="acc_type",length = 10)
	@Enumerated(EnumType.STRING)
	private AccountTypeEnum accountType;




	public Long getAccId() {
		return accId;
	}

	public void setAccId(Long accId) {
		this.accId = accId;
	}

	public String getAccShtDesc() {
		return accShtDesc;
	}

	public void setAccShtDesc(String accShtDesc) {
		this.accShtDesc = accShtDesc;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public boolean isVatAppli() {
		return vatAppli;
	}

	public void setVatAppli(boolean vatAppli) {
		this.vatAppli = vatAppli;
	}

	public BigDecimal getVatRate() {
		return vatRate;
	}

	public void setVatRate(BigDecimal vatRate) {
		this.vatRate = vatRate;
	}

	public boolean isWhtxAppl() {
		return whtxAppl;
	}

	public void setWhtxAppl(boolean whtxAppl) {
		this.whtxAppl = whtxAppl;
	}

	public BigDecimal getWhtaxVal() {
		return whtaxVal;
	}

	public void setWhtaxVal(BigDecimal whtaxVal) {
		this.whtaxVal = whtaxVal;
	}

	public BigDecimal getCommRate() {
		return commRate;
	}

	public void setCommRate(BigDecimal commRate) {
		this.commRate = commRate;
	}

	public AccountTypeEnum getAccountType() {
		return accountType;
	}

	public void setAccountType(AccountTypeEnum accountType) {
		this.accountType = accountType;
	}
}

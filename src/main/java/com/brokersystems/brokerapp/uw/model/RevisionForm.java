package com.brokersystems.brokerapp.uw.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;


public class RevisionForm {
	
	@NotNull
	private Long policyId;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date effectiveDate;
	
	@NotNull
	private String revisionType;
	
	private String policyNumber;
	
	private String revisionNo;

	private  String cancellationType;

	private BigDecimal amount;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	private Date effToDate;

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	private String remarks;

	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}

	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getRevisionType() {
		return revisionType;
	}

	public void setRevisionType(String revisionType) {
		this.revisionType = revisionType;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public Date getEffToDate() {
		if(effToDate==null) return new Date();
		else
			return effToDate;
	}

	public void setEffToDate(Date effToDate) {
		this.effToDate = effToDate;
	}

	public String getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(String revisionNo) {
		this.revisionNo = revisionNo;
	}

	public String getCancellationType() {
		return cancellationType;
	}

	public void setCancellationType(String cancellationType) {
		this.cancellationType = cancellationType;
	}

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

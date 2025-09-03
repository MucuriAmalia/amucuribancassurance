package com.brokersystems.brokerapp.uw.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;


public class ReinstateForm {
	
	@NotNull
	private Long policyId;

	
	private String policyNumber;
	
	private String revisionNo;

	
	public Long getPolicyId() {
		return policyId;
	}

	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}


	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}


	public String getRevisionNo() {
		return revisionNo;
	}

	public void setRevisionNo(String revisionNo) {
		this.revisionNo = revisionNo;
	}

}

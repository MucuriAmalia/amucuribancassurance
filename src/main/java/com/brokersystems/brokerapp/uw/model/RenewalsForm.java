package com.brokersystems.brokerapp.uw.model;

import javax.validation.constraints.NotNull;

public class RenewalsForm {
	
	@NotNull
	private Long policyId;
	
	private String policyNumber;

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

	private String businessType;

	public String getBusinessType() {
		return businessType;
	}

	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}


	

}

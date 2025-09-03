package com.brokersystems.brokerapp.uw.model;

import java.util.List;

public class BatchRenewalForm {
	
	private List<Long> renPolicies;
	
	private List<RenewalRecord> renewals;
	
	public List<Long> getRenPolicies() {
		return renPolicies;
	}

	public void setRenPolicies(List<Long> renPolicies) {
		this.renPolicies = renPolicies;
	}

	public List<RenewalRecord> getRenewals() {
		return renewals;
	}

	public void setRenewals(List<RenewalRecord> renewals) {
		this.renewals = renewals;
	}

	
	
	
	

}

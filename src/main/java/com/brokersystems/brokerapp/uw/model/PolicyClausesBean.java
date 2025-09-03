package com.brokersystems.brokerapp.uw.model;

import java.util.List;

public class PolicyClausesBean {
	
	private Long polId;
	
	private List<Long> clauses;

	public Long getPolId() {
		return polId;
	}

	public void setPolId(Long polId) {
		this.polId = polId;
	}

	public List<Long> getClauses() {
		return clauses;
	}

	public void setClauses(List<Long> clauses) {
		this.clauses = clauses;
	}
	
	

}

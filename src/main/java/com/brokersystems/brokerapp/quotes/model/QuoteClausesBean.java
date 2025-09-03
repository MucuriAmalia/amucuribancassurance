package com.brokersystems.brokerapp.quotes.model;

import java.util.List;

public class QuoteClausesBean {
	
	private Long quoteProdId;
	
	private List<Long> clauses;

	public Long getQuoteProdId() {
		return quoteProdId;
	}

	public void setQuoteProdId(Long quoteProdId) {
		this.quoteProdId = quoteProdId;
	}

	public List<Long> getClauses() {
		return clauses;
	}

	public void setClauses(List<Long> clauses) {
		this.clauses = clauses;
	}
	
	

}

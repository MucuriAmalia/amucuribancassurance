package com.brokersystems.brokerapp.uw.model;

import java.util.List;

public class RiskBean {
	
	private Long riskId;
	
	private List<RiskSectionBean> sections;

	public Long getRiskId() {
		return riskId;
	}

	public void setRiskId(Long riskId) {
		this.riskId = riskId;
	}

	public List<RiskSectionBean> getSections() {
		return sections;
	}

	public void setSections(List<RiskSectionBean> sections) {
		this.sections = sections;
	}
	
	

}

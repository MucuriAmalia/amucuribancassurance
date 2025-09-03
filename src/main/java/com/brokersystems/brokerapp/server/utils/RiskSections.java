package com.brokersystems.brokerapp.server.utils;

import java.util.List;
import java.util.Set;

public class RiskSections {
	
	private Set<RiskImportBean> risks;
	private List<SectionImportBean> sections;
	public Set<RiskImportBean> getRisks() {
		return risks;
	}
	public void setRisks(Set<RiskImportBean> risks) {
		this.risks = risks;
	}
	public List<SectionImportBean> getSections() {
		return sections;
	}
	public void setSections(List<SectionImportBean> sections) {
		this.sections = sections;
	}
	@Override
	public String toString() {
		return "RiskSections [risks=" + risks + ", sections=" + sections + "]";
	}
	
	

}

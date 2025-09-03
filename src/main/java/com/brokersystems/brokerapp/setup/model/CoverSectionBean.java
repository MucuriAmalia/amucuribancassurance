package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class CoverSectionBean {
	
	private Long coverCode;
	
	private List<Long> sections;

	public Long getCoverCode() {
		return coverCode;
	}

	public void setCoverCode(Long coverCode) {
		this.coverCode = coverCode;
	}

	public List<Long> getSections() {
		return sections;
	}

	public void setSections(List<Long> sections) {
		this.sections = sections;
	}
	
	

}

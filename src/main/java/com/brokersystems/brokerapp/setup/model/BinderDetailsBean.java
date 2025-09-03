package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class BinderDetailsBean {
	
    private Long bindCode;
	
	private List<Long> coverTypes;

	public Long getBindCode() {
		return bindCode;
	}

	public void setBindCode(Long bindCode) {
		this.bindCode = bindCode;
	}

	public List<Long> getCoverTypes() {
		return coverTypes;
	}

	public void setCoverTypes(List<Long> coverTypes) {
		this.coverTypes = coverTypes;
	}
	
	

}

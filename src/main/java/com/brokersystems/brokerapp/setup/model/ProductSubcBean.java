package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class ProductSubcBean {
	
    private Long proCode;
	
	private List<Long> subclasses;

	public Long getProCode() {
		return proCode;
	}

	public void setProCode(Long proCode) {
		this.proCode = proCode;
	}

	public List<Long> getSubclasses() {
		return subclasses;
	}

	public void setSubclasses(List<Long> subclasses) {
		this.subclasses = subclasses;
	}
	
	

}

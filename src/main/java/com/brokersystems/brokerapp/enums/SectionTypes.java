package com.brokersystems.brokerapp.enums;

public enum SectionTypes {
	  
	SI("SI","Section Sum Insured",1),
	ES("ES","Extension Sum Insured",3),
	EL("ES","Extension Limit",4),
	SL("SL","Section Limit",2),
	FS("ES","No FAP Section Limit",5),
	LD("LD","Loading",6),
	DS("DS","Discount",7),
	LS("LS","Loading SI",8),
	RD("RD","Section Rider",9);
	
	
    private String code;

	private Integer order;
	
	private String value;

	private SectionTypes(String code,String value,Integer order) {
		this.code = code;
		this.value = value;
		this.order = order;
	}

	public String getCode() {
		return code;
	}

	public String getValue() {
		return value;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}

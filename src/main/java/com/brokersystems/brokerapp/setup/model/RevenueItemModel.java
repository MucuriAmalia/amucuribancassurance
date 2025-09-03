package com.brokersystems.brokerapp.setup.model;

import java.util.List;

public class RevenueItemModel {
	
	private Long prgCode;
	
	private List<RevenueItemBean> items;

	public Long getPrgCode() {
		return prgCode;
	}

	public void setPrgCode(Long prgCode) {
		this.prgCode = prgCode;
	}

	public List<RevenueItemBean> getItems() {
		return items;
	}

	public void setItems(List<RevenueItemBean> items) {
		this.items = items;
	}
	
	

}

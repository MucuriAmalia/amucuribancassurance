package com.brokersystems.brokerapp.enums;

import org.springframework.stereotype.Component;

@Component
public class RevenueItemFinder {
	
	public RevenueItems getRevenueItem(String item){
		RevenueItems revItem = RevenueItems.valueOf(item);
		return revItem;
	}

}

package com.brokersystems.brokerapp.uw.service;

import java.io.IOException;
import java.math.BigDecimal;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

public interface PremComputeService {
	
	 void computePrem(Long polCode) throws BadRequestException, IOException;
	
	 void computeCancelPrem(Long polCode) throws BadRequestException;
	
	 BigDecimal computeRiskPrem(Long riskCode);
	
	 BigDecimal calculateTax(BigDecimal premAmount,BigDecimal rate, BigDecimal divFactor,String rateType);
	
	 BigDecimal getCommissionRate(long BinderDet,BigDecimal premAmount);
	
	 void computeEndorsePremium(Long polCode) throws BadRequestException,IOException;

	 void computeLifePrem(Long polCode) throws BadRequestException, IOException;

	void computeCancelLifePrem(Long polCode) throws BadRequestException, IOException;


}

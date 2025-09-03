package com.brokersystems.brokerapp.setup.service;

import java.math.BigDecimal;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.ParametersDef;

public interface ParamService {
	
	DataTablesResult<ParametersDef> findAllParameters(DataTablesRequest request)  throws IllegalAccessException;
	
	void createParameter(ParametersDef paramDef) throws BadRequestException;
	
	public void deleteParameter(Long paramCode);
	
	public String getParameterString(String paramName) throws BadRequestException;
	
	public BigDecimal getParamValue(String paramName) throws BadRequestException;

	public Boolean getParamBoolean(String paramName) throws BadRequestException;

	public Integer getParamInt(String paramName) throws BadRequestException;

}

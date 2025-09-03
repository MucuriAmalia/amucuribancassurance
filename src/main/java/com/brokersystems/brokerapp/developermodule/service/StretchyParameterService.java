package com.brokersystems.brokerapp.developermodule.service;

import com.brokersystems.brokerapp.developermodule.dto.StretchyParameterDTO;
import com.brokersystems.brokerapp.developermodule.model.StretchyParameter;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

public interface StretchyParameterService {

    StretchyParameter createStretchyParam(StretchyParameterDTO stretchyParameter) throws BadRequestException;

    DataTablesResult<StretchyParameterDTO> findAllStretchyParameters(DataTablesRequest request) throws IllegalAccessException;

    void deleteStretchyParameter(Long stpId);


}

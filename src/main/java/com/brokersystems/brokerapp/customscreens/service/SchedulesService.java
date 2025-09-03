package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.customscreens.model.GenericResultsetData;
import com.brokersystems.brokerapp.customscreens.model.RegisteredTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.fasterxml.jackson.databind.JsonNode;

public interface SchedulesService {

    DataTablesResult<RegisteredTable> getSubclassSchedules(DataTablesRequest pageable, Long subId);

    GenericResultsetData retrieveDataTableGenericResultSet(final Long subId, final Long riskId) throws BadRequestException;

    GenericResultsetData retrieveSingleDataTableGenericResultSet(final Long subId) throws BadRequestException;

    void saveSchedule(Long subId, JsonNode data) throws BadRequestException;



}

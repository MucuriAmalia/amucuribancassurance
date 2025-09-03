package com.brokersystems.brokerapp.customscreens.service;

import com.brokersystems.brokerapp.customscreens.model.ResultsetColumnHeaderData;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.util.List;

public interface GenericDataService {

    List<ResultsetColumnHeaderData> fillResultsetColumnHeaders(String datatable) throws BadRequestException;


}

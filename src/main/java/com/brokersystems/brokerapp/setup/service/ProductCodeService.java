package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.model.ProductCodes;


public interface ProductCodeService {
    DataTablesResult<ProductCodes> findAllProductCodes(DataTablesRequest request) throws IllegalAccessException;
}

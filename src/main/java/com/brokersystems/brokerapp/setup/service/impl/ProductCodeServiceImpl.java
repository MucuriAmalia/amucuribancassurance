package com.brokersystems.brokerapp.setup.service.impl;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.setup.model.ProductCodes;
import com.brokersystems.brokerapp.setup.model.QProductCodes;
import com.brokersystems.brokerapp.setup.repository.ProductCodesRepo;
import com.brokersystems.brokerapp.setup.service.ProductCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProductCodeServiceImpl implements ProductCodeService {

    @Autowired
    private ProductCodesRepo productCodesRepo;
    @Override
    public DataTablesResult<ProductCodes> findAllProductCodes(DataTablesRequest request) throws IllegalAccessException {
        Page<ProductCodes> page = productCodesRepo.findAll(request.searchPredicate(QProductCodes.productCodes), request);
        return new DataTablesResult<>(request, page);
    }
}

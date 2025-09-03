package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.io.IOException;

public interface BulkPremComputeService {

    void computeUploadLifePrem(Long polCode) throws BadRequestException, IOException;
    void computeUploadGeneralPrem(Long polCode) throws BadRequestException, IOException;

}

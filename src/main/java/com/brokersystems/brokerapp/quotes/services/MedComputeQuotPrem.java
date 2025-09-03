package com.brokersystems.brokerapp.quotes.services;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by waititu on 17/08/2017.
 */
public interface MedComputeQuotPrem {
    public void computePrem(Long quotCode) throws BadRequestException,FileNotFoundException,IOException;
}

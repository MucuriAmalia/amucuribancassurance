package com.brokersystems.brokerapp.medical.service;

import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Created by peter on 4/27/2017.
 */
public interface MedicalComputePrem {

    public void computePrem(Long polCode ) throws BadRequestException,FileNotFoundException,IOException;

}

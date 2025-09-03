package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditCardDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.CreditShieldDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CreditShieldService {

    void  uploadCreditShield(File file, Long userId) throws BadRequestException, IOException, InvalidFormatException;

    DataTablesResult<CreditShieldDTO> findUnprocessedCreditShield(DataTablesRequest request);

    DataTablesResult<CreditShieldDTO> viewUnprocessedCreditShield(DataTablesRequest request);

    PolicyTrans processSingleCreditShieldPol(Long shieldId, Long userId,boolean isApproved) throws BadRequestException;

    List<Long> processBulkCreditShieldPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleCreditShieldPol(Long policyId) throws BadRequestException;

    String approveBulkCreditShieldPol(List<Long> policyIds) throws BadRequestException;

    String deleteBulkCreditShieldPol(List<Long> shieldIds) throws  BadRequestException;

    String deleteProcessedBulkCreditShieldPol(List<Long> policyIds) throws BadRequestException;

    List<Long> getAllCreditShieldIds();
}

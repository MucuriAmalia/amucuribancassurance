package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.MortgageLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface MortgageLifeService {

    void uploadMortgageLife(File file, Long userId) throws BadRequestException;

    DataTablesResult<MortgageLifeDTO> findUnprocessedMortgageLife(DataTablesRequest request);

    DataTablesResult<MortgageLifeDTO> viewUnprocessedMortgageLife(DataTablesRequest request);

    PolicyTrans processSingleMortgageLifePol(Long mortgageId,Long userId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkMortgageLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleMortgageLifePol(Long policyId) throws BadRequestException;

    String approveBulkMortgageLifePol(List<Long> policyIds) throws BadRequestException;

    String deleteBulkMortgageLifePol(List<Long> mortgageIds) throws BadRequestException;

    String bulkDelProcessedPolicies(List<Long> mortgageIds) throws BadRequestException;
}

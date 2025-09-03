package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.bulktransactions.dtos.TransProcessingAuthDTO;
import com.brokersystems.brokerapp.bulktransactions.models.TransactionProcessing;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface TransProcessingService {

    Map<String, Object> uploadAndSaveExcelData(MultipartFile file) throws BadRequestException;

    DataTablesResult<TransactionProcessing> findUnprocessedTrans(DataTablesRequest request);
    DataTablesResult<TransProcessingAuthDTO> findUnauthorizedPolicies(DataTablesRequest request);

    String processSingleTrans(Long transId) throws BadRequestException;

    String bulkProcessTrans(List<Long> transactionIds) throws BadRequestException;

    String approveSingleLoadedPolicy(Long policyId) throws BadRequestException;
    String approveBulkLoadedPolicy(List<Long> policyIds) throws BadRequestException;

    String deleteBulkTransProcessing(List<Long> transIds)throws BadRequestException;
}

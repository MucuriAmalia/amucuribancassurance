package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface CreditLifeService {

    void uploadCreditLife(File file, Long userId) throws BadRequestException;

    DataTablesResult<CreditLifeDTO> findUnprocessedCreditLife(DataTablesRequest request);

    DataTablesResult<CreditLifeDTO> viewUnprocessedCreditLife(DataTablesRequest request);

    PolicyTrans processSingleCreditLifePol(Long creditId,Long userId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkCreditLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleCreditLifePol(Long policyId) throws BadRequestException;

    String approveBulkCreditLifePol(List<Long> policyIds) throws BadRequestException;

     String deleteUploadCreditLife(List<Long> creditIds);

    String deleteProcessedBulkCreditLifePol(List<Long> policyIds) throws BadRequestException;
}

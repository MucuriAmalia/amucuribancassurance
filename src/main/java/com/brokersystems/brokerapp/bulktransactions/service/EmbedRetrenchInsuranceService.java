package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedRetrenchInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface EmbedRetrenchInsuranceService {

    void uploadEmbedRetrench(File file, Long userId) throws BadRequestException;

    DataTablesResult<EmbedRetrenchInsuranceDTO> findUnprocessedEmbedRetrench(DataTablesRequest request);

    DataTablesResult<EmbedRetrenchInsuranceDTO> viewUnprocessedEmbedRetrench(DataTablesRequest request);

    PolicyTrans processSingleEmbedRetrenchPol(Long retrenchId,Long userId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkEmbedRetrenchPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleEmbedRetrenchPol(Long policyId) throws BadRequestException;

    String approveBulkEmbedRetrenchPol(List<Long> policyIds) throws BadRequestException;

    String deleteBulkEmbedRetrenchPol(List<Long> retrenchIds) throws BadRequestException;

    String bulkDelProcessedPolicies(List<Long> retrenchIds) throws BadRequestException;
}

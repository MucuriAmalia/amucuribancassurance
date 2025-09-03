package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.EmbedPackageInsuranceDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface EmbedPackageInsuranceService {
    void uploadEmbedFirstAss(File file, Long userId) throws BadRequestException;

    DataTablesResult<EmbedPackageInsuranceDTO> findUnprocessedPackagedInsur(DataTablesRequest request);

    DataTablesResult<EmbedPackageInsuranceDTO> viewUnprocessedEmbedPackageInsur(DataTablesRequest request);

    PolicyTrans processSinglePackagedInsurPol(Long embedId, Long userId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkPackagedInsurPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSinglePackagedInsurPol(Long policyId) throws BadRequestException;

    String approveBulkPackagedInsurPol(List<Long> policyIds) throws BadRequestException;

    String deleteBulkPackagedInsurPol(List<Long> embedIds) throws BadRequestException;

    String bulkDelProcessedPolicies(List<Long> embedIds) throws BadRequestException;
}

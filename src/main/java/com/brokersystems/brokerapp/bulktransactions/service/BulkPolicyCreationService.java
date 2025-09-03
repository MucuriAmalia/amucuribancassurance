package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.bulktransactions.dtos.BulkPolicyCreationDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BulkPolicyCreationService {

    Map<String, Object> uploadBulkPolicy(MultipartFile file) throws BadRequestException;
    DataTablesResult<BulkPolicyCreationDTO> findUnProcessedBulkPol(DataTablesRequest request);

    PolicyTrans processSingleBulkPolicy(Long bulkId, boolean isApproved) throws BadRequestException;

    DataTablesResult<BulkPolicyCreationDTO> viewBulkPolicies(DataTablesRequest request);

    List<Long> bulkProcessPolicies(List<Long> bulkIds, boolean isApproved) throws BadRequestException;

    String approveSinglePol(Long policyId) throws BadRequestException;

    String approveBulkPol(List<Long> policyIds) throws BadRequestException;

    void bulkDelUploadPolicies(List<Long> bulkIds) throws BadRequestException;

    String bulkDelProcessedPolicies(List<Long> bulkIds) throws BadRequestException;
}

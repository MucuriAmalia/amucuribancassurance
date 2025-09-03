package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.GroupLifeDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface GroupLifeService {

    void uploadGroupLife(File file, Long userId) throws BadRequestException;

    DataTablesResult<GroupLifeDTO> findUnprocessedGroupLife(DataTablesRequest request);

    DataTablesResult<GroupLifeDTO> viewUnprocessedGroupLife(DataTablesRequest request);

    PolicyTrans processSingleGroupLifePol(Long groupId, Long userId,boolean isApproved) throws BadRequestException;

    List<Long> processBulkGroupLifePol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String deleteBulkGroupLifePol(List<Long> groupIds) throws BadRequestException;

    String bulkDelProcessedPolicies(List<Long> groupIds) throws BadRequestException;

    String approveSingleGroupLifePol(Long policyId) throws BadRequestException;

    String approveBulkGroupLifePol(List<Long> policyIds) throws BadRequestException;

}

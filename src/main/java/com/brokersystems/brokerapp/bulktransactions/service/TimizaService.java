package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.TimizaDTO;
import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface TimizaService {

    void uploadTimiza(File file, Long userId) throws BadRequestException;

    DataTablesResult<TimizaDTO> findUnprocessedTimiza(DataTablesRequest request);

    DataTablesResult<TimizaDTO> viewUnprocessedTimiza(DataTablesRequest request);

    PolicyTrans processSingleTimizaPol(Long groupId,Long userId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkTimizaPol(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleTimizaPol(Long policyId) throws BadRequestException;

    String approveBulkTimizaPol(List<Long> policyIds) throws BadRequestException;

    String deleteUploadBulkTimizaPols(List<Long> timizaIds);

    String deleteProcessedTimizaPols(List<Long> policyIds) throws BadRequestException;
}

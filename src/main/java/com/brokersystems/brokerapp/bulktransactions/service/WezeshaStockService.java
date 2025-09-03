package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.bulktransactions.dtos.WezeshaStockDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface WezeshaStockService {

    Map<String, Object> uploadWezeshaStock(MultipartFile file) throws BadRequestException;

    DataTablesResult<WezeshaStockDTO> findUnProcessedWezeshaPol(DataTablesRequest request);

    DataTablesResult<WezeshaStockDTO> viewWezeshaPolicies(DataTablesRequest request);

    PolicyTrans processSingleWezeshPol(Long wezeshaId,Long userId, boolean isApproved) throws BadRequestException;

    List<Long> bulkProcessWezeshaPolicies(BatchCreditBatch batchCreditBatch, boolean isApproved) throws BadRequestException;

    String approveSingleWezeshaPol(Long policyId) throws BadRequestException;

    String approveBulkWezeshaPol(List<Long> policyIds) throws BadRequestException;

    String deleteUploadWezeshaStock(List<Long> policyIds);

    String deleteProcessedWezeshaStock(List<Long> policyIds) throws BadRequestException;
}

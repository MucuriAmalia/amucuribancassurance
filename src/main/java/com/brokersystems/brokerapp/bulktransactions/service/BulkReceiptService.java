package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.models.BatchCreditBatch;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.users.dto.MakerCheckDTO;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface BulkReceiptService {

    void uploadBulkReceipt(File file, Long userID) throws BadRequestException;

    DataTablesResult<ReceiptsDTO> findUnprocessedBulkReceipt(DataTablesRequest request);

    DataTablesResult<MakerCheckDTO> findProcessedBulkReceipt(DataTablesRequest request);

    String approveBulkReceipting(BatchCreditBatch batchCreditBatch) throws BadRequestException;

    String deleteBulkReceipting(List<Long> receiptIds) throws BadRequestException;

    String approveBulkReceiptingTaskId(BatchCreditBatch batchCreditBatch) throws BadRequestException;

    String rejectBulkReceiptingTaskId(BatchCreditBatch batchCreditBatch) throws BadRequestException;
}

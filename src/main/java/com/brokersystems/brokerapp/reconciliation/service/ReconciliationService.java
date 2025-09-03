package com.brokersystems.brokerapp.reconciliation.service;

import com.brokersystems.brokerapp.reconciliation.Reconciliation;
import com.brokersystems.brokerapp.reconciliation.ReconciliationDTO;
import com.brokersystems.brokerapp.reconciliation.TransReconciliation;
import com.brokersystems.brokerapp.reconciliation.TransReconciliationDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReconciliationService {
    @Transactional
    void processReconExcel(MultipartFile file) throws IOException;

    @Transactional
    void processTransReconExcel(MultipartFile file) throws IOException;

    @Transactional
    DataTablesResult<ReconciliationDTO> getReconciliationData(@DataTable DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo);

    @Transactional
    List<Reconciliation> reconcileData(List<ReconciliationDTO> reconciliationDataList) throws BadRequestException;

    @Transactional
    DataTablesResult<ReconciliationDTO> getReconciledData(DataTablesRequest request,Long accountCode,Date dateFrom,Date dateTo);

    @Transactional
    DataTablesResult<TransReconciliationDTO> getTransReconciliationData(@DataTable DataTablesRequest request, Long accountCode, Date dateFrom, Date dateTo,String unifiedSearch);

    @Transactional
    List<TransReconciliation> transReconcileData(List<TransReconciliationDTO> transReconciliationDataList) throws BadRequestException;

    @Transactional
    DataTablesResult<TransReconciliationDTO> getTransReconciledData(DataTablesRequest request,Long accountCode,Date dateFrom,Date dateTo,String unifiedSearch);

    void deleteTransReconciledData(Long accountCode, Date dateFrom, Date dateTo, String unifiedSearch) throws BadRequestException;
}

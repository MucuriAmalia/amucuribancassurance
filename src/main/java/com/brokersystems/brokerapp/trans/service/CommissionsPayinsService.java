package com.brokersystems.brokerapp.trans.service;

import com.brokersystems.brokerapp.reconciliation.ReconciliationDTO;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.trans.dtos.CommissionDTO;
import com.brokersystems.brokerapp.trans.dtos.CommissionReconciliationDTO;
import com.brokersystems.brokerapp.trans.dtos.CommissionUnreconciledDataDTO;
import com.brokersystems.brokerapp.trans.model.CommissionData;
import com.brokersystems.brokerapp.trans.model.CommissionReconciliationData;
import com.brokersystems.brokerapp.trans.model.CommissionTrans;
import com.brokersystems.brokerapp.uw.model.RiskUploadForm;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface CommissionsPayinsService {

    void importCommissions(MultipartFile file, RiskUploadForm uploadForm) throws IOException,BadRequestException;



    DataTablesResult<CommissionTrans> findAllLoadedCommissions(DataTablesRequest request,Long acct) throws IllegalAccessException;

    void processCommissions(CommissionData commissionData) throws BadRequestException;

    void confirmCommissions(Long commReceipt) throws BadRequestException;

    void undoProcessCommissions(Long receiptId) throws BadRequestException;

    public File getCommissionsTemplate() throws BadRequestException;

    void authorizeCommissions(Long transId) throws BadRequestException;

//    DataTablesResult<CommissionTrans> findAllConfirmedCommissions(DataTablesRequest pageable) throws IllegalAccessException;

    DataTablesResult<CommissionDTO> findAllConfirmedCommissions(Long agent, DataTablesRequest pageable) throws IllegalAccessException ;

    @Transactional
    void processCommExcel(MultipartFile file, Long acctId) throws IOException, BadRequestException;

    @Transactional
    DataTablesResult<CommissionReconciliationDTO> getCommissionReconciliationData(DataTablesRequest request, Long accountCode);

    @Transactional(readOnly = true)
    DataTablesResult<CommissionUnreconciledDataDTO> getUnreconciledCommissionData(DataTablesRequest request, Long accountCode);


    @Transactional
    List<CommissionReconciliationData> reconcileCommissionData(List<CommissionReconciliationDTO> commissionReconciliationData) throws BadRequestException;

    void deleteCommission(List<CommissionReconciliationDTO> commissionReconciliationData) throws BadRequestException;

}

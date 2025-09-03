package com.brokersystems.brokerapp.trans.service;

import com.brokersystems.brokerapp.accounts.dtos.PayeesDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDTO;
import com.brokersystems.brokerapp.trans.dtos.ChequeTransDtlsDTO;
import com.brokersystems.brokerapp.trans.dtos.RejectRequistionForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {

    void createRequistion(ChequeTransDTO chequeTransDTO) throws BadRequestException;

    Page<PayeesDTO> findPayeeAccounts(String searchValue, Pageable pageable);
    Page<BankAccountDTO> findAllBankAccountsLov(String searchValue, Pageable pageable);
    DataTablesResult<ChequeTransDTO> findRequistions(DataTablesRequest request, String type);
    DataTablesResult<ChequeTransDtlsDTO> findRequistionDetails(DataTablesRequest request, Long reqNo);

    void rejectRequistion(RejectRequistionForm rejectRequistionForm) throws BadRequestException;
    void rejectApprovedRequistion(RejectRequistionForm rejectRequistionForm) throws BadRequestException;
    void authRequistion(Long reqId) throws BadRequestException;
    void updateRequistion(Long reqId) throws BadRequestException;

}

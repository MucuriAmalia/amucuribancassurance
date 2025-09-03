package com.brokersystems.brokerapp.setup.service;

import com.brokersystems.brokerapp.claims.dtos.ClaimantsDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.BankAccountDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BankService {

    DataTablesResult<BankAccountDTO> findAllBankAccounts(DataTablesRequest request);

    void createBankAccount(BankAccountDTO bankAccountDTO) throws BadRequestException;

    void deleteBankAccount(long accountId) throws BadRequestException;

}

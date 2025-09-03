package com.brokersystems.brokerapp.bulktransactions.service;

import com.brokersystems.brokerapp.bulktransactions.dtos.CreditCardDTO;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface CreditCardService {

    Map<String, Object> uploadCreditCard(MultipartFile file) throws BadRequestException;

    DataTablesResult<CreditCardDTO> findUnprocessedCreditCard(DataTablesRequest request);

    DataTablesResult<CreditCardDTO> viewUnprocessedCreditCard(DataTablesRequest request);

    PolicyTrans processSingleCreditCardPol(Long cardId, boolean isApproved) throws BadRequestException;

    List<Long> processBulkCreditCardPol(List<Long> cardIds, boolean isApproved) throws BadRequestException;

    String approveSingleCreditCardPol(Long policyId) throws BadRequestException;

    String approveBulkCreditCardPol(List<Long> policyIds) throws BadRequestException;

    String deleteBulkCreditCardPol(List<Long> cardIds) throws  BadRequestException;

    String deleteProcessedBulkCreditCardPol(List<Long> policyIds) throws BadRequestException;
}

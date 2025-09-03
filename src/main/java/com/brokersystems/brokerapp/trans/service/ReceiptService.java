package com.brokersystems.brokerapp.trans.service;

import com.brokersystems.brokerapp.accounts.dtos.SystemTransDTO;
import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.bulktransactions.models.BulkReceipt;
import com.brokersystems.brokerapp.medical.model.SelfFundParams;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.dtos.LifeReceiptsDTO;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


public interface ReceiptService {
	
	DataTablesResult<ReceiptTrans> findAllReceipts(DataTablesRequest request) throws IllegalAccessException;

	Page<SystemTransDTO>  findReceiptTransactions(String paramString,Long agentId, Pageable request) throws IllegalAccessException;

	Page<SystemTransactions> findAgentCommisionTrans(String paramString, Pageable paramPageable, Long insuranceId) throws IllegalAccessException;

	Page<SystemTransactions> findCreditorCommisionTrans(String paramString, Pageable paramPageable) throws IllegalAccessException;

	Long createReceipt(ReceiptTrans receipt, boolean isApproved) throws BadRequestException;

    Long createBulkReceipt(ReceiptTrans receipt, boolean isApproved, BulkReceipt bulkReceipt, List<Long> checkerIds) throws BadRequestException;

	void markReceiptPrinted(Long receiptId, User user) throws BadRequestException;
	DataTablesResult<LifeReceiptsDTO> findPolicyReceipts(Long polId, DataTablesRequest request);

	public Page<CollectionAccounts> findCollectionAccts(String paramString, Pageable paramPageable);

	Page<SelfFundParams> findSelfFundTransactions(String paramString, Pageable paramPageable) throws IllegalAccessException;

	Page<PolicyTrans> findLifeTransactions(String paramString, Pageable paramPageable) throws IllegalAccessException;

	 void postReceiptAccount(ReceiptTrans receiptTrans, SystemTrans systemTrans, long subCode, BigDecimal amount, BigDecimal whtx, BigDecimal totalComm, AccountDef accountDef,PolicyTrans policyTrans, int count) throws BadRequestException;

	 void postReceiptAccount( SystemTrans systemTrans, PolicyTrans policyTrans, BigDecimal allocAmt) throws BadRequestException;

	Long createFundReceipt(ReceiptTrans receipt) throws BadRequestException;

	DataTablesResult<ReceiptTrans> findPrintedReceipts(DataTablesRequest request, Date from,Date to) throws IllegalAccessException;

	DataTablesResult<ReceiptTrans> findUnPrintedReceipts(DataTablesRequest request, Date from,Date to) throws IllegalAccessException;

	void createReceipts(List<Long> receipts);

	void deleteCertTrans();

	void markReceiptsPrinted(List<Long> receipts) throws BadRequestException;

	DataTablesResult<ReceiptTrans> findReceiptsToCancel(DataTablesRequest request, Date from,Date to,String refNo,String receiptNo,String policyNo,Long clientId) throws IllegalAccessException;

//	void cancelReceipts(List<CancelData> receipts) throws BadRequestException;

	void cancelReceipts(List<CancelData> receipts, boolean isApproved) throws BadRequestException;

	DataTablesResult<IntegrationDtls> findIntegrationDtls(DataTablesRequest request, String receipted) throws IllegalAccessException;

	void updateIntegrationDtls(IntegrationDtls integrationDtls);

	BigDecimal getPolicyTotalRcptAmount(String policyno) throws BadRequestException;




}

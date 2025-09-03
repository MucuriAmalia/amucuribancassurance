package com.brokersystems.brokerapp.trans.service;

import java.math.BigDecimal;

import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.SystemTrans;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

public interface AllocationService {
	
	public void allocateReceipt(Long receiptId, User user) throws BadRequestException;

	public void allocateCommReceipt(Long receiptId, User user) throws BadRequestException;

	 void allocateLifeReceipt(Long receiptId, User user) throws BadRequestException;
	public void deallocateLifeReceipt(Long receiptId, User user,SystemTransactions transact) throws BadRequestException;

	void saveSubMarketer(Long polId,BigDecimal marketerComm, BigDecimal subAgentComm, SystemTrans transaction,SystemTransactions parentTRans, SystemTransactions receiptTrans );
	void saveSubMarketerWithoutGl(Long polId,BigDecimal marketerComm, BigDecimal subAgentComm, SystemTrans transaction,SystemTransactions parentTRans, SystemTransactions receiptTrans );

	public void allocateCredit(Long transId,Long debitTransId) throws BadRequestException;
	
	public SystemTransactions createReceiptTransaction(String refNo, BigDecimal receiptAmt, BigDecimal receiptbalance,
			Currencies currency,ClientDef client,AccountDef accnt, SystemTransactions prevTrans,SystemTrans transaction,String narration,BigDecimal settleAmt, User user , PolicyTrans policy,
													  String type, String controlAcc );

	public SystemTransactions createInstalmentTransaction(String refNo, BigDecimal AllocAmount, SystemTrans transaction,
														  String narration,  PolicyTrans policy, boolean reversal, Long installNo);
	public void createSettlements( SystemTransactions debit, SystemTransactions credit, BigDecimal amount,User user)
			throws BadRequestException;

	void createLifeSettlements(SystemTransactions debit, SystemTransactions credit, BigDecimal amount,User user)
			throws BadRequestException;

	public void autoAllocateContra(Long transId,Long debitTransId) throws BadRequestException;

	public void createFundReceiptTransaction(long receiptId);

	public void deallocateReceipt(Long receiptId, String comment) throws BadRequestException;

	public void createClientSettlements(SystemTransactions debit, SystemTransactions credit, BigDecimal amount,User user)
			throws BadRequestException;

	public void allocateCashBasisTrans(Long polCode, User user) throws BadRequestException;
}

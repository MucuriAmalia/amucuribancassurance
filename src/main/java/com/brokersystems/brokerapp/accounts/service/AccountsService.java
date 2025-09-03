package com.brokersystems.brokerapp.accounts.service;

import com.brokersystems.brokerapp.accounts.dtos.*;
import com.brokersystems.brokerapp.accounts.dtos.SettlementDTO;
import com.brokersystems.brokerapp.accounts.model.*;
import com.brokersystems.brokerapp.server.datatables.DataTable;
import com.brokersystems.brokerapp.server.datatables.DataTablesRequest;
import com.brokersystems.brokerapp.server.datatables.DataTablesResult;
import com.brokersystems.brokerapp.server.exception.BadRequestException;
import com.brokersystems.brokerapp.setup.dto.CoaSubAccountsDTO;
import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import com.brokersystems.brokerapp.setup.model.RequiredDocBean;
import com.brokersystems.brokerapp.trans.model.*;
import com.brokersystems.brokerapp.uw.dtos.ReceiptsDTO;
import com.brokersystems.brokerapp.uw.dtos.RefundClientDTO;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by peter on 3/25/2017.
 */
public interface AccountsService {

    List<SettlementDTO> findAllocationDetails(Long agentCode, Long currencyCode, Date wefDate, Date wetDate, String pstatus) throws IllegalAccessException;
    List<SettlementDTO> findAllocationCommDetails(Long agentCode, Long currencyCode, Date wefDate, Date wetDate, String commtype) throws IllegalAccessException;
    List<SettlementDTO> findSubAgentCommDetails(Long subAgentCode, Long currencyCode, Date wefDate, Date wetDate) throws IllegalAccessException;
    List<SettlementDTO> findSubAgentTransactions(Long agentCode,  Date wefDate,Date wetDate, Long subAcctId) throws IllegalAccessException;
    public BigDecimal getPayableAmount(String receiptRef,String debitRef, BigDecimal allocAmt) throws BadRequestException;
    public BigDecimal getPayableSubAgentCommAmount(String receiptRef,String debitRef, BigDecimal locAmt, BigDecimal allocAmt) throws BadRequestException;
    public BigDecimal getPayableCommAmount(String receiptRef,String debitRef, BigDecimal locAmt, BigDecimal allocAmt) throws BadRequestException;
    public BigDecimal getSumAuditAmt(Long allocId) throws BadRequestException;

    public void consolidatePayments(ProcessingBean processingBean) throws BadRequestException;

     void consolidateCreditorCommissionsPayments(ProcessingBean processingBean) throws BadRequestException;

     void consolidateCommissionPayments(ProcessingBean processingBean) throws BadRequestException;
     void consolidateSubAgentCommissionPayments(ProcessingBean processingBean) throws BadRequestException;

    DataTablesResult<SystemTransactions> findAgentsTransactions(DataTablesRequest request,Long agentCode,Long currencyCode) throws IllegalAccessException;
    DataTablesResult<SystemTransactions> findCreditorCommTransactions(DataTablesRequest request,Long agentCode,Long currencyCode,String transType) throws IllegalAccessException;
    DataTablesResult<SystemTransactions> findBulkCreditorCommTransactions(DataTablesRequest request,Long agentCode,Long currencyCode) throws IllegalAccessException;
    DataTablesResult<SystemTransactions> findSubAgentCommTransactions(DataTablesRequest request,List<Long> agentCode,Long currencyCode) throws IllegalAccessException;

    DataTablesResult<SystemTransactions> findIAAgentsTransactions(DataTablesRequest request,Long agentCode,Long currencyCode) throws IllegalAccessException;

    DataTablesResult<PaymentAudit> findTransPaymentAudits(DataTablesRequest request,Long transId) throws IllegalAccessException;

    Map<String,Object> findAllocationCommDetails(Long transNo) throws IllegalAccessException;
    Map<String,Object> findAllocationSubAgentCommDetails(Long transNo) throws IllegalAccessException;

    public void deleteAudit(Long auditId) throws BadRequestException;

    public void cancelPaymentTrans(Long transNo) throws BadRequestException;
    public void rejectAuthTrans(Long transNo) throws BadRequestException;

    DataTablesResult<SystemTransDTO> findAuthorizeTrans(DataTablesRequest request) throws IllegalAccessException;

     void authAccountTrans(Long transId) throws BadRequestException;
    public void approveCreditorComm(Long transId) throws BadRequestException;
    public void approveSubAgentComm(Long transId) throws BadRequestException;

    DataTablesResult<SystemTransactions> findCreditTrans(DataTablesRequest request,Long clientCode,Long curCode,Long brnCode,String allTrans) throws IllegalAccessException;

    DataTablesResult<ReceiptsDTO> findPolicyCreditTrans(Long policyId, DataTablesRequest request) throws IllegalAccessException;

    DataTablesResult<SystemTransactions> findDebitTrans(DataTablesRequest request,Long clientCode,Long curCode,Long brnCode,String allTrans) throws IllegalAccessException;

    public BigDecimal getAllocateAmount(Long transno) throws BadRequestException;

    void allocateCreditTrans(Long crTransNo,Long drTransNo,BigDecimal allocAmount) throws BadRequestException;

    public void allocateCreditTransRfnd(Long crTransNo, Long drTransNo, BigDecimal allocAmount) throws BadRequestException;

    DataTablesResult<CoaMainAccounts> findChartOfAccounts(DataTablesRequest request) throws IllegalAccessException;

    DataTablesResult<CoaSubAccountsDTO> findChartSubAccounts(DataTablesRequest request, Long mainAccId) throws IllegalAccessException;


    void defineCoa(CoaMainAccounts mainAccount) throws BadRequestException;

    void deleteCoa(Long coId);

    void defineSubCoa(CoaSubAccounts subAccount) throws BadRequestException;

    void deleteSubCoa(Long subCoaId);

    DataTablesResult<Banks> findBanks(DataTablesRequest request) throws IllegalAccessException;

    DataTablesResult<BankBranches> findBankBranches(DataTablesRequest request,Long bnId) throws IllegalAccessException;

    void defineBank(Banks mainAccount) throws BadRequestException;

    void deleteBank(Long bnId);

    void defineBankBranch(BankBranches bankBranch) throws BadRequestException;

    void deleteBankBranch(Long bbId);

    DataTablesResult<CollectionAccounts> findCollectionAccts(DataTablesRequest request,Long pmId) throws IllegalAccessException;

    void defineCollectAccount(CollectionAccounts collectionAccounts) throws BadRequestException;

    void deleteCollectAcct(Long caId);

    public Page<PaymentModes> findPaymentModes(String paramString, Pageable paramPageable);

    Page<BankBranchDTO> findBankBranches(String paramString, Pageable paramPageable);
    Page<AccountingPeriodDTO> findAccountingPeriods(String paramString, Pageable paramPageable);

    public Page<PolicyTrans> findClientPolicies(String paramString, Pageable paramPageable, Long clientId);
    DataTablesResult<ReceiptSettlementDetails> findCreditSettlements(DataTablesRequest request, Long crCode) throws IllegalAccessException;
    public Page<SystemTransactions> findPolicyTrans(String paramString, Pageable paramPageable, Long policyId);

    public void createRefund(Refunds refund, String refundStatus) throws BadRequestException;

    public void makeRefundReady(Long refundId) throws BadRequestException;

    public void rejectRefund(Long refundId , String remarks) throws BadRequestException;

    public void authorizeRefund(Long refundId,String account ) throws BadRequestException;


    public DataTablesResult<Refunds> findRefundTrans(DataTablesRequest request, Date from, Date to,String refNo,String status,String policyNo,Long clientId) throws IllegalAccessException;

    Page<AccountDef> findSubAgentAccounts(String paramString, Pageable paramPageable);

    public void deleteRefund(Long refId) throws BadRequestException;
    Page<SystemTransactions> findInsuranceReceipts(String paramString, Long accountId,Pageable paramPageable);
    DataTablesResult<PayeesDTO> findAllPayees(DataTablesRequest request);
    DataTablesResult<FinalReportFormatDTO> findFinalReportFormats(String type, DataTablesRequest request);
    DataTablesResult<FinalReportFormatTotalDTO> findFinalReportFormatsTotals(String type, DataTablesRequest request);
    DataTablesResult<FinalReportFormatAcctsDTO> findFinalReportFormatsAccts(Long formatId, DataTablesRequest request);
    DataTablesResult<FinalReportFormatAcctsDTO> findFinalReportFormatsGroupAccts(Long formatId, DataTablesRequest request);
    DataTablesResult<PayeeAccountsDTO> findAllPayeesAccounts(Long payeeId, DataTablesRequest request);
    void createPayee(PayeesDTO payeesDTO) throws BadRequestException;
    void createPayeeAccount(PayeeAccountsDTO payeesDTO) throws BadRequestException;
    DataTablesResult<OpeningBalanceDTO> findAllOpeningBalances(DataTablesRequest request) throws BadRequestException;
    void createAccountingYear(AccountYearDTO accountYearDTO) throws BadRequestException;
    Map<String,Long> getCurrentAccountPeriod();
    DataTablesResult<AccountYearDTO> findAccountingYears(Long year,Long branchCode, DataTablesRequest request);
    DataTablesResult<AccountingPeriodDTO> findAccountingYearsPeriods(Long yearId, DataTablesRequest request);
    void createReportFormatAccount(FinalReportFormatAcctsDTO acctsDTO) throws BadRequestException;
    void createReportFormatGroupAccount(FinalReportFormatAcctsDTO acctsDTO) throws BadRequestException;

     DataTablesResult<RefundClientDTO> findClientRefunds(DataTablesRequest request, Long clientCode, Long prodCode, String policyNo) throws IllegalAccessException;

    Map<String, Object> processRefunds(List<Long> transactionNumbers, String refundComments) throws IllegalAccessException;

    void createRefundRequiredDocs(RequiredDocBean requiredDocBean);

    DataTablesResult<GlTransactions> findAllGlTransactions(
            DataTablesRequest request,
            String accountNo,
            String asAtDate) throws IllegalAccessException;

}
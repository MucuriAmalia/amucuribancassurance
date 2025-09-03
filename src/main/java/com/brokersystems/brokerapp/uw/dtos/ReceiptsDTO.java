package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brokersystems.brokerapp.accounts.model.CollectionAccounts;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.brokersystems.brokerapp.trans.model.SystemTransactions;
import com.brokersystems.brokerapp.trans.model.SystemTransactionsTemp;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

public class ReceiptsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptsDTO.class);

    private String receiptNo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date receiptDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date receiptTransDate;
    private BigDecimal receiptAmount;
    private CollectionAccounts collectionAccount;
    private OrgBranch branch;
    private String currencies;
    private String paidBy;
    private String paymentRef;
    private String manualRef;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private Date documentDate;
    private String receiptDesc;
    private String receiptType;
    private ArrayList<ReceiptTransDtls> details;
    private ArrayList<SystemTransactions> transactions;
    private ArrayList<SystemTransactionsTemp> transactionsTemps;
    private ArrayList<PolicyTrans> policyTrans;
    private Long receiptBulkId;
    private String insurerName;
    private String narration;
    private String branchName;
    private Long brnCode;

    private String cancelComment;

    public String getCancelComment() {
        return cancelComment;
    }

    public Long getBrnCode() {
        return brnCode;
    }

    public void setBrnCode(Long brnCode) {
        this.brnCode = brnCode;
    }

    public String getNarration() {
        return narration;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getInsurerName() {
        return insurerName;
    }

    public Long getReceiptBulkId() {
        return receiptBulkId;
    }

    public void setReceiptBulkId(Long receiptBulkId) {
        this.receiptBulkId = receiptBulkId;
    }

    public ArrayList<SystemTransactionsTemp> getTransactionsTemps() {
        return transactionsTemps;
    }

    public void setTransactionsTemps(ArrayList<SystemTransactionsTemp> transactionsTemps) {
        this.transactionsTemps = transactionsTemps;
    }

    public String getReceiptNo() {
        return receiptNo;
    }

    public Date getReceiptDate() {
        return receiptDate;
    }

    public void setReceiptDate(Date receiptDate) {
        this.receiptDate = receiptDate;
    }

    public BigDecimal getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(BigDecimal receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public Date getReceiptTransDate() {
        return receiptTransDate;
    }

    public void setReceiptTransDate(Date receiptTransDate) {
        this.receiptTransDate = receiptTransDate;
    }

    public CollectionAccounts getCollectionAccount() {
        return collectionAccount;
    }

    public void setCollectionAccount(CollectionAccounts collectionAccount) {
        this.collectionAccount = collectionAccount;
    }


    public String getCurrencies() {
        return currencies;
    }

    public String getPaidBy() {
        return paidBy;
    }

    public String getPaymentRef() {
        return paymentRef;
    }

    public String getManualRef() {
        return manualRef;
    }

    public Date getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(Date documentDate) {
        this.documentDate = documentDate;
    }

    public String getReceiptDesc() {
        return receiptDesc;
    }

    public ArrayList<ReceiptTransDtls> getDetails() {
        return details;
    }

    public void setDetails(ArrayList<ReceiptTransDtls> details) {
        this.details = details;
    }

    public String getReceiptType() {
        return receiptType;
    }


    public ArrayList<SystemTransactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<SystemTransactions> transactions) {
        this.transactions = transactions;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public ArrayList<PolicyTrans> getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(ArrayList<PolicyTrans> policyTrans) {
        this.policyTrans = policyTrans;
    }

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setReceiptNo(String receiptNo) {
        validateAndSanitize(receiptNo, REF_PATTERN, "Receipt Number");
        this.receiptNo = receiptNo == null ? null : StringEscapeUtils.escapeHtml4(receiptNo.trim());
    }

    public void setCurrencies(String currencies) {
        validateAndSanitize(currencies, NAME_PATTERN, "Currencies");
        this.currencies = currencies == null ? null : StringEscapeUtils.escapeHtml4(currencies.trim());
    }

    public void setPaidBy(String paidBy) {
        validateAndSanitize(paidBy, NAME_PATTERN, "Paid By");
        this.paidBy = paidBy == null ? null : StringEscapeUtils.escapeHtml4(paidBy.trim());
    }

    public void setPaymentRef(String paymentRef) {
        validateAndSanitize(paymentRef, REF_PATTERN, "Payment Reference");
        this.paymentRef = paymentRef == null ? null : StringEscapeUtils.escapeHtml4(paymentRef.trim());
    }

    public void setManualRef(String manualRef) {
        validateAndSanitize(manualRef, REF_PATTERN, "Manual Reference");
        this.manualRef = manualRef == null ? null : StringEscapeUtils.escapeHtml4(manualRef.trim());
    }

    public void setReceiptDesc(String receiptDesc) {
        validateAndSanitize(receiptDesc, DESC_PATTERN, "Receipt Description");
        this.receiptDesc = receiptDesc == null ? null : StringEscapeUtils.escapeHtml4(receiptDesc.trim());
    }

    public void setReceiptType(String receiptType) {
        validateAndSanitize(receiptType, NAME_PATTERN, "Receipt Type");
        this.receiptType = receiptType == null ? null : StringEscapeUtils.escapeHtml4(receiptType.trim());
    }

    public void setInsurerName(String insurerName) {
        validateAndSanitize(insurerName, NAME_PATTERN, "Insurer Name");
        this.insurerName = insurerName == null ? null : StringEscapeUtils.escapeHtml4(insurerName.trim());
    }

    public void setNarration(String narration) {
        validateAndSanitize(narration, DESC_PATTERN, "Narration");
        this.narration = narration == null ? null : StringEscapeUtils.escapeHtml4(narration.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setCancelComment(String cancelComment) {
        validateAndSanitize(cancelComment, DESC_PATTERN, "Cancel Comment");
        this.cancelComment = cancelComment == null ? null : StringEscapeUtils.escapeHtml4(cancelComment.trim());
    }
}

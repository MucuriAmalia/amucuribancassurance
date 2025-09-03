package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.*;

public class BankAccountDTO {

    private static final Logger logger = LoggerFactory.getLogger(BankAccountDTO.class);

    private Long baId;
    private Long branchId;
    private Long bankBranchId;
    private String branchName;
    private String bankBranchName;
    private String bankAcctNumber;
    private String bankAcctName;
    private String currentChequeNo;
    private Long glId;
    private String glCode;
    private String glName;
    private String status;
    private BigDecimal maximumAmt;
    private BigDecimal minimumAmt;

    public String getGlCode() {
        return glCode;
    }

    public Long getBankBranchId() {
        return bankBranchId;
    }

    public void setBankBranchId(Long bankBranchId) {
        this.bankBranchId = bankBranchId;
    }

    public String getBankBranchName() {
        return bankBranchName;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getGlName() {
        return glName;
    }

    public Long getBaId() {
        return baId;
    }

    public void setBaId(Long baId) {
        this.baId = baId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBankAcctNumber() {
        return bankAcctNumber;
    }

    public String getBankAcctName() {
        return bankAcctName;
    }

    public String getCurrentChequeNo() {
        return currentChequeNo;
    }

    public Long getGlId() {
        return glId;
    }

    public void setGlId(Long glId) {
        this.glId = glId;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getMaximumAmt() {
        return maximumAmt;
    }

    public void setMaximumAmt(BigDecimal maximumAmt) {
        this.maximumAmt = maximumAmt;
    }

    public BigDecimal getMinimumAmt() {
        return minimumAmt;
    }

    public void setMinimumAmt(BigDecimal minimumAmt) {
        this.minimumAmt = minimumAmt;
    }

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setGlCode(String glCode) {
        validateAndSanitize(glCode, REF_PATTERN, "GL Code");
        this.glCode = glCode == null ? null : StringEscapeUtils.escapeHtml4(glCode.trim());
    }

    public void setBankBranchName(String bankBranchName) {
        validateAndSanitize(bankBranchName, DESC_PATTERN, "Bank Branch Name");
        this.bankBranchName = bankBranchName == null ? null : StringEscapeUtils.escapeHtml4(bankBranchName.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, DESC_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setGlName(String glName) {
        validateAndSanitize(glName, NAME_PATTERN, "GL Name");
        this.glName = glName == null ? null : StringEscapeUtils.escapeHtml4(glName.trim());
    }

    public void setBankAcctNumber(String bankAcctNumber) {
        validateAndSanitize(bankAcctNumber, REF_PATTERN, "Bank Account Number");
        this.bankAcctNumber = bankAcctNumber == null ? null : StringEscapeUtils.escapeHtml4(bankAcctNumber.trim());
    }

    public void setBankAcctName(String bankAcctName) {
        validateAndSanitize(bankAcctName, NAME_PATTERN, "Bank Account Name");
        this.bankAcctName = bankAcctName == null ? null : StringEscapeUtils.escapeHtml4(bankAcctName.trim());
    }

    public void setCurrentChequeNo(String currentChequeNo) {
        validateAndSanitize(currentChequeNo, REF_PATTERN, "Current Cheque Number");
        this.currentChequeNo = currentChequeNo == null ? null : StringEscapeUtils.escapeHtml4(currentChequeNo.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

}

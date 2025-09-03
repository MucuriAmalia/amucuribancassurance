package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.ToString;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@ToString
public class CommissionReconciliationDTO {

    private static final Logger logger = LoggerFactory.getLogger(CommissionReconciliationDTO.class);

    private String policyNumber;
    private String clientName;
    private String debitRefNo;
    private String creditRefNo;
    private String revisionNo;
    private String transCode;
    private String underwriterPolicyNo;
    private BigDecimal payment;
    private BigDecimal commission;
    private BigDecimal totalRevenue;
    private BigDecimal withholdingTax;
    private BigDecimal adminFeeWhtx;
    private BigDecimal adminFee;
    private BigDecimal payableCommission;
    private Long underwriterCode;
    private Long currencyCode;
    private Long reconId;


    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
    public BigDecimal getAdminFeeWhtx() {
        return adminFeeWhtx;
    }

    public void setAdminFeeWhtx(BigDecimal adminFeeWhtx) {
        this.adminFeeWhtx = adminFeeWhtx;
    }

    public BigDecimal getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(BigDecimal adminFee) {
        this.adminFee = adminFee;
    }

    public String getTransCode() {
        return transCode;
    }


    public String getUnderwriterPolicyNo() {
        return underwriterPolicyNo;
    }


    public Long getReconId() {
        return reconId;
    }

    public void setReconId(Long reconId) {
        this.reconId = reconId;
    }

    public Long getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Long currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Long getUnderwriterCode() {
        return underwriterCode;
    }

    public void setUnderwriterCode(Long underwriterCode) {
        this.underwriterCode = underwriterCode;
    }

    public String getPolicyNumber() {
        return policyNumber;
    }


    public String getClientName() {
        return clientName;
    }


    public String getDebitRefNo() {
        return debitRefNo;
    }


    public String getCreditRefNo() {
        return creditRefNo;
    }

    public String getRevisionNo() {
        return revisionNo;
    }


    public BigDecimal getPayment() {
        return payment;
    }

    public void setPayment(BigDecimal payment) {
        this.payment = payment;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public BigDecimal getWithholdingTax() {
        return withholdingTax;
    }

    public void setWithholdingTax(BigDecimal withholdingTax) {
        this.withholdingTax = withholdingTax;
    }

    public BigDecimal getPayableCommission() {
        return payableCommission;
    }

    public void setPayableCommission(BigDecimal payableCommission) {
        this.payableCommission = payableCommission;
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

    public void setPolicyNumber(String policyNumber) {
        validateAndSanitize(policyNumber, REF_PATTERN, "Policy Number");
        this.policyNumber = policyNumber == null ? null : StringEscapeUtils.escapeHtml4(policyNumber.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setDebitRefNo(String debitRefNo) {
        validateAndSanitize(debitRefNo, REF_PATTERN, "Debit Reference Number");
        this.debitRefNo = debitRefNo == null ? null : StringEscapeUtils.escapeHtml4(debitRefNo.trim());
    }

    public void setCreditRefNo(String creditRefNo) {
        validateAndSanitize(creditRefNo, REF_PATTERN, "Credit Reference Number");
        this.creditRefNo = creditRefNo == null ? null : StringEscapeUtils.escapeHtml4(creditRefNo.trim());
    }
    public void setRevisionNo(String revisionNo) {
        validateAndSanitize(revisionNo, REF_PATTERN, "Revision Number");
        this.revisionNo = revisionNo == null ? null : StringEscapeUtils.escapeHtml4(revisionNo.trim());
    }


    public void setTransCode(String transCode) {
        validateAndSanitize(transCode, REF_PATTERN, "Transaction Code");
        this.transCode = transCode == null ? null : StringEscapeUtils.escapeHtml4(transCode.trim());
    }

    public void setUnderwriterPolicyNo(String underwriterPolicyNo) {
        validateAndSanitize(underwriterPolicyNo, REF_PATTERN, "Underwriter Policy Number");
        this.underwriterPolicyNo = underwriterPolicyNo == null ? null : StringEscapeUtils.escapeHtml4(underwriterPolicyNo.trim());
    }


}

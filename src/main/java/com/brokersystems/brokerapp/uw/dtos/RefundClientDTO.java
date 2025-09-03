package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

public class RefundClientDTO {

    private static final Logger logger = LoggerFactory.getLogger(RefundClientDTO.class);

    private Long transno;
    private String clientFname;
    private String clientOtherNames;
    private String policyNo;
    private String riskNoteNumber;
    private String transRefNo;
    private String transType;
    private String productDesc;
    private BigDecimal settleAmt;
    private BigDecimal balance;
    private Date transDate;
    private Long tenId;
    private Long riskId;

    public RefundClientDTO(Long transno, Date transDate, String clientFname, String clientOtherNames,
                           String policyNo, String riskNoteNumber, String transRefNo, String transType,
                           String productDesc, BigDecimal settleAmt, BigDecimal balance, Long riskId) {

        this.transno = transno;
        this.transDate = transDate;
        this.setClientFname(clientFname);
        this.setClientOtherNames(clientOtherNames);
        this.setPolicyNo(policyNo);
        this.setRiskNoteNumber(riskNoteNumber);
        this.setTransRefNo(transRefNo);
        this.setTransType(transType);
        this.setProductDesc(productDesc);
        this.settleAmt = settleAmt != null ? settleAmt : BigDecimal.ZERO;
        this.balance = balance != null ? balance.abs() : BigDecimal.ZERO;
        this.riskId = riskId;
    }

    // Getters and Setters
    public Long getTransno() { return transno; }
    public void setTransno(Long transno) { this.transno = transno; }
    public Date getTransDate() { return transDate; } public void setTransDate(Date transDate) { this.transDate = transDate; }
    public String getClientFname() { return clientFname; }
    public String getClientOtherNames() { return clientOtherNames; }
    public String getPolicyNo() { return policyNo; }
    public String getRiskNoteNumber() { return riskNoteNumber; }
    public String getTransRefNo() { return transRefNo; }
    public String getTransType() { return transType; }
    public String getProductDesc() { return productDesc; }
    public BigDecimal getSettleAmt() { return settleAmt; }
    public void setSettleAmt(BigDecimal settleAmt) { this.settleAmt = settleAmt; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Long getTenId() { return tenId; }
    public void setTenId(Long tenId) { this.tenId = tenId; }
    public Long getRiskId() { return riskId; }
    public void setRiskId(Long riskId) { this.riskId = riskId; }



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

    public void setClientFname(String clientFname) {
        validateAndSanitize(clientFname, NAME_PATTERN, "Client First Name");
        this.clientFname = clientFname == null ? null : StringEscapeUtils.escapeHtml4(clientFname.trim());
    }

    public void setClientOtherNames(String clientOtherNames) {
        validateAndSanitize(clientOtherNames, NAME_PATTERN, "Client Other Names");
        this.clientOtherNames = clientOtherNames == null ? null : StringEscapeUtils.escapeHtml4(clientOtherNames.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setRiskNoteNumber(String riskNoteNumber) {
        validateAndSanitize(riskNoteNumber, REF_PATTERN, "Risk Note Number");
        this.riskNoteNumber = riskNoteNumber == null ? null : StringEscapeUtils.escapeHtml4(riskNoteNumber.trim());
    }

    public void setTransRefNo(String transRefNo) {
        validateAndSanitize(transRefNo, REF_PATTERN, "Transaction Reference Number");
        this.transRefNo = transRefNo == null ? null : StringEscapeUtils.escapeHtml4(transRefNo.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setProductDesc(String productDesc) {
        validateAndSanitize(productDesc, DESC_PATTERN, "Product Description");
        this.productDesc = productDesc == null ? null : StringEscapeUtils.escapeHtml4(productDesc.trim());
    }
}

package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommissionUnreconciledDataDTO {

    private static final Logger logger = LoggerFactory.getLogger(CommissionUnreconciledDataDTO.class);

    private Long id;
    private String policyNumber;
    private String clientName;
    private String debitRefNo;
    private String creditRefNo;
    private String revisionNo;
    private BigDecimal payment;
    private BigDecimal commission;
    private BigDecimal withholdingTax;
    private BigDecimal payableCommission;
    private String unreconciledReason;
    private String systemClientName;
    private BigDecimal systemCommission;
    private BigDecimal systemWhtx;
    private BigDecimal systemPayment;
    private Date createdDate;
    private String batchReference;

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


    public void setUnreconciledReason(String unreconciledReason) {
        validateAndSanitize(unreconciledReason, DESC_PATTERN, "Unreconciled Reason");
        this.unreconciledReason = unreconciledReason == null ? null : StringEscapeUtils.escapeHtml4(unreconciledReason.trim());
    }

    public void setSystemClientName(String systemClientName) {
        validateAndSanitize(systemClientName, NAME_PATTERN, "System Client Name");
        this.systemClientName = systemClientName == null ? null : StringEscapeUtils.escapeHtml4(systemClientName.trim());
    }

    public void setBatchReference(String batchReference) {
        validateAndSanitize(batchReference, REF_PATTERN, "Batch Reference");
        this.batchReference = batchReference == null ? null : StringEscapeUtils.escapeHtml4(batchReference.trim());
    }
}

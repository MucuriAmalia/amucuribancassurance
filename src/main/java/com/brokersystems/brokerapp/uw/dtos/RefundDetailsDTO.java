package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

public class RefundDetailsDTO {

    private static final Logger logger = LoggerFactory.getLogger(RefundDetailsDTO.class);

    private Long reverseTransId;
    private Long originalTransId;
    private BigDecimal refundAmount;
    private String clientName;
    private String policyNo;
    private String originalTransType;
    private String refundReason;
    private String requestedBy;
    private Date requestDate;

    // Constructors
    public RefundDetailsDTO() {}

    // Getters and Setters
    public Long getReverseTransId() { return reverseTransId; }
    public void setReverseTransId(Long reverseTransId) { this.reverseTransId = reverseTransId; }

    public Long getOriginalTransId() { return originalTransId; }
    public void setOriginalTransId(Long originalTransId) { this.originalTransId = originalTransId; }

    public BigDecimal getRefundAmount() { return refundAmount; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }

    public String getClientName() { return clientName; }

    public String getPolicyNo() { return policyNo; }

    public String getOriginalTransType() { return originalTransType; }

    public String getRefundReason() { return refundReason; }

    public String getRequestedBy() { return requestedBy; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

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


    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setOriginalTransType(String originalTransType) {
        validateAndSanitize(originalTransType, NAME_PATTERN, "Original Transaction Type");
        this.originalTransType = originalTransType == null ? null : StringEscapeUtils.escapeHtml4(originalTransType.trim());
    }

    public void setRefundReason(String refundReason) {
        validateAndSanitize(refundReason, DESC_PATTERN, "Refund Reason");
        this.refundReason = refundReason == null ? null : StringEscapeUtils.escapeHtml4(refundReason.trim());
    }

    public void setRequestedBy(String requestedBy) {
        validateAndSanitize(requestedBy, NAME_PATTERN, "Requested By");
        this.requestedBy = requestedBy == null ? null : StringEscapeUtils.escapeHtml4(requestedBy.trim());
    }
}


package com.brokersystems.brokerapp.uw.dtos;

import lombok.Data;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Data
public class PolicyCancellationDTO {

    private static final Logger logger = LoggerFactory.getLogger(PolicyCancellationDTO.class);

    private Long policyId;
    private String remarks;
    private Long cancelReasonId;
    private BigDecimal refundAmount;


    // Constructors
    public PolicyCancellationDTO() {}

    // Getters and Setters
    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getRemarks() {
        return remarks;
    }


    public Long getCancelReasonId() {
        return cancelReasonId;
    }

    public void setCancelReasonId(Long cancelReasonId) {
        this.cancelReasonId = cancelReasonId;
    }


    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
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

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }
}

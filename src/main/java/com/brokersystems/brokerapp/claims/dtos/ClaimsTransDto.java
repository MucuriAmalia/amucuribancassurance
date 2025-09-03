package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Slf4j
public class ClaimsTransDto {

    @Setter
    private Long transId;

    @Setter
    private BigDecimal transAmount;

    @Setter
    private Date transDate;

    private String transType;

    @Setter
    private Date authDate;

    private String createdBy;

    private String authBy;

    private String transStatus;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }

    public void setAuthBy(String authBy) {
        validateAndSanitize(authBy, "Authorized By");
        this.authBy = authBy == null ? null : StringEscapeUtils.escapeHtml4(authBy.trim());
    }

    public void setTransStatus(String transStatus) {
        validateAndSanitize(transStatus, "Transaction Status");
        this.transStatus = transStatus == null ? null : StringEscapeUtils.escapeHtml4(transStatus.trim());
    }
}
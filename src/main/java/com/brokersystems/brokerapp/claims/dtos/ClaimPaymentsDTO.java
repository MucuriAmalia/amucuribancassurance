package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
@Slf4j
public class ClaimPaymentsDTO {

    @Setter
    private Long clmPymntId;

    private String payee;

    private String reference;

    private String paymentMode;

    private String transType;

    private String currency;

    @Setter
    private BigDecimal amount;

    private String status;

    private String raisedBy;

    @Setter
    private Date raisedDate;

    @Setter
    private Date authDate;

    private String authBy;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPayee(String payee) {
        validateAndSanitize(payee, NAME_PATTERN, "Payee");
        this.payee = payee == null ? null : StringEscapeUtils.escapeHtml4(payee.trim());
    }

    public void setReference(String reference) {
        validateAndSanitize(reference, REF_PATTERN, "Reference");
        this.reference = reference == null ? null : StringEscapeUtils.escapeHtml4(reference.trim());
    }

    public void setPaymentMode(String paymentMode) {
        validateAndSanitize(paymentMode, NAME_PATTERN, "Payment Mode");
        this.paymentMode = paymentMode == null ? null : StringEscapeUtils.escapeHtml4(paymentMode.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, NAME_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setRaisedBy(String raisedBy) {
        validateAndSanitize(raisedBy, NAME_PATTERN, "Raised By");
        this.raisedBy = raisedBy == null ? null : StringEscapeUtils.escapeHtml4(raisedBy.trim());
    }

    public void setAuthBy(String authBy) {
        validateAndSanitize(authBy, NAME_PATTERN, "Authorized By");
        this.authBy = authBy == null ? null : StringEscapeUtils.escapeHtml4(authBy.trim());
    }
}
package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
public class CommissionDTO {

    private static final Logger logger = LoggerFactory.getLogger(CommissionDTO.class);

    private final Long transId;
    private final String receiptNo;
    private final Date receiptDate;
    private final String insuranceCo;
    private final BigDecimal totalCommission;
    private final String status;
    private final BigDecimal originalAmount;
    private final BigDecimal balance;


    private CommissionDTO(final Long transId, final String receiptNo, final Date receiptDate, final String insuranceCo, final BigDecimal totalCommission, final String status, final BigDecimal originalAmount, final BigDecimal balance) {
        this.transId = transId;
        validateAndSanitize(receiptNo, REF_PATTERN, "Receipt Number");
        this.receiptNo = receiptNo == null ? null : StringEscapeUtils.escapeHtml4(receiptNo.trim());
        this.receiptDate = receiptDate;
        validateAndSanitize(insuranceCo, NAME_PATTERN, "Insurance Company");
        this.insuranceCo = insuranceCo == null ? null : StringEscapeUtils.escapeHtml4(insuranceCo.trim());
        this.totalCommission = totalCommission;
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
        this.originalAmount = originalAmount;
        this.balance = balance;
    }

    public static CommissionDTO instance(final Long transId,final String receiptNo, final Date receiptDate, final String insuranceCo, final BigDecimal totalCommission, final String status, final BigDecimal originalAmount, final BigDecimal balance) {
        return new CommissionDTO(transId,receiptNo, receiptDate, insuranceCo, totalCommission, status, originalAmount, balance);
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

}

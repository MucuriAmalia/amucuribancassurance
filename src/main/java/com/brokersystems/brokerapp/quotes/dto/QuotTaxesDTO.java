package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
public class QuotTaxesDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuotTaxesDTO.class);

    @Setter
    private Long polTaxId;
    private String revItemCode;
    @Setter
    private BigDecimal taxAmount;
    @Setter
    private BigDecimal taxRate;
    @Setter
    private BigDecimal divFactor;
    private String rateType;
    private String taxLevel;
    private String quotStatus;

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

    public void setRevItemCode(String revItemCode) {
        validateAndSanitize(revItemCode, REF_PATTERN, "Revenue Item Code");
        this.revItemCode = revItemCode == null ? null : StringEscapeUtils.escapeHtml4(revItemCode.trim());
    }

    public void setRateType(String rateType) {
        validateAndSanitize(rateType, REF_PATTERN, "Rate Type");
        this.rateType = rateType == null ? null : StringEscapeUtils.escapeHtml4(rateType.trim());
    }

    public void setTaxLevel(String taxLevel) {
        validateAndSanitize(taxLevel, REF_PATTERN, "Tax Level");
        this.taxLevel = taxLevel == null ? null : StringEscapeUtils.escapeHtml4(taxLevel.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, REF_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }
}

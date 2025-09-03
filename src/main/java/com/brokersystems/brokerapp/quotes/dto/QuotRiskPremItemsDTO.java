package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuotRiskPremItemsDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuotRiskPremItemsDTO.class);

    private Long sectId;
    private BigDecimal prem;
    private BigDecimal amount;
    private BigDecimal rate;
    private BigDecimal divFactor;
    private BigDecimal freeLimit;
    private BigDecimal annualEarnings;
    private Long secId;
    private Long rateId;
    private Long riskId;
    private String secName;
    private String quotStatus;
    private Long quoteId;

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


    public void setSecName(String secName) {
        validateAndSanitize(secName, NAME_PATTERN, "Section Name");
        this.secName = secName == null ? null : StringEscapeUtils.escapeHtml4(secName.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, REF_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }

}

package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Getter
public class QuotClausesDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuotClausesDTO.class);

    @Setter
    private Long subClauseId;
    private String header;

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


    public void setHeader(String header) {
        validateAndSanitize(header, DESC_PATTERN, "Header");
        this.header = header == null ? null : StringEscapeUtils.escapeHtml4(header.trim());
    }
}

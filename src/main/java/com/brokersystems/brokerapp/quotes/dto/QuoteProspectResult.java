package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
public class QuoteProspectResult {

    private static final Logger logger = LoggerFactory.getLogger(QuoteProspectResult.class);

    private String result;
    @Setter
    private Long prospectId;
    @Setter
    private Long polId;

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


    public void setResult(String result) {
        validateAndSanitize(result, REF_PATTERN, "Result");
        this.result = result == null ? null : StringEscapeUtils.escapeHtml4(result.trim());
    }
}

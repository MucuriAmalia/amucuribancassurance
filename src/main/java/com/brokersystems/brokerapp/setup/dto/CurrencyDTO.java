package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

public class CurrencyDTO {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyDTO.class);

    private Long curCode;
    private String curName;

    public Long getCurCode() {
        return curCode;
    }

    public void setCurCode(Long curCode) {
        this.curCode = curCode;
    }

    public String getCurName() {
        return curName;
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

    public void setCurName(String curName) {
        validateAndSanitize(curName, NAME_PATTERN, "Currency Name");
        this.curName = curName == null ? null : StringEscapeUtils.escapeHtml4(curName.trim());
    }
}

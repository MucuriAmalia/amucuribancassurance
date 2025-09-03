package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CountryDTO {

    private static final Logger logger = LoggerFactory.getLogger(CountryDTO.class);

    private Long couCode;
    private String couName;

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

    public void setCouName(String couName) {
        validateAndSanitize(couName, NAME_PATTERN, "Country Name");
        this.couName = couName == null ? null : StringEscapeUtils.escapeHtml4(couName.trim());
    }

}

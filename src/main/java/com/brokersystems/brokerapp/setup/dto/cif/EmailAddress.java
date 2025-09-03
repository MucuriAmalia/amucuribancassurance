package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.EMAIL_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailAddress {

    private static final Logger logger = LoggerFactory.getLogger(EmailAddress.class);

    private String emailAddress;

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

    public void setEmailAddress(String emailAddress) {
        validateAndSanitize(emailAddress, EMAIL_PATTERN,"Email Address" );
        this.emailAddress = emailAddress == null ? null : StringEscapeUtils.escapeHtml4(emailAddress.trim());
    }
}

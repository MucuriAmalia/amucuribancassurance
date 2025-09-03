package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResidentialAddress {

    private static final Logger logger = LoggerFactory.getLogger(ResidentialAddress.class);

    private String addressTypeCode;
    private String countryCode;

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

    public void setAddressTypeCode(String addressTypeCode) {
        validateAndSanitize(addressTypeCode, NAME_PATTERN, "Address Type Code");
        this.addressTypeCode = addressTypeCode == null ? null : StringEscapeUtils.escapeHtml4(addressTypeCode.trim());
    }

    public void setCountryCode(String countryCode) {
        validateAndSanitize(countryCode, NAME_PATTERN, "Country Code");
        this.countryCode = countryCode == null ? null : StringEscapeUtils.escapeHtml4(countryCode.trim());
    }
}

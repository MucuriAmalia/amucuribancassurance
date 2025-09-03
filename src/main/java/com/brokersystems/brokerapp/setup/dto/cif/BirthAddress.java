package com.brokersystems.brokerapp.setup.dto.cif;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.ADDRESS_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BirthAddress {
    private static final Logger logger = LoggerFactory.getLogger(BirthAddress.class);

    private String birthPlace;

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

    public void setBirthPlace(String birthPlace) {
        validateAndSanitize(birthPlace, ADDRESS_PATTERN, "Birth Place");
        this.birthPlace = birthPlace == null ? null : StringEscapeUtils.escapeHtml4(birthPlace.trim());
    }
}

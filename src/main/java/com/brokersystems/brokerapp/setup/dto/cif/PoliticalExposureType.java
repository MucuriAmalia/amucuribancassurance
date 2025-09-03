package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PoliticalExposureType {

    private static final Logger logger = LoggerFactory.getLogger(PoliticalExposureType.class);

    private String pepFlag;

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

    public void setPepFlag(String pepFlag) {
        validateAndSanitize(pepFlag, REF_PATTERN, "PEP Flag");
        this.pepFlag = pepFlag == null ? null : StringEscapeUtils.escapeHtml4(pepFlag.trim());
    }
}

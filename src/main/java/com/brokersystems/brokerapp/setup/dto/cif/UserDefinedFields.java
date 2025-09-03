package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDefinedFields {

    private static final Logger logger = LoggerFactory.getLogger(UserDefinedFields.class);

    private String udfCode;
    private String udfType;

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

    public void setUdfCode(String udfCode) {
        validateAndSanitize(udfCode, REF_PATTERN ,"UDF Code");
        this.udfCode = udfCode == null ? null : StringEscapeUtils.escapeHtml4(udfCode.trim());
    }

    public void setUdfType(String udfType) {
        validateAndSanitize(udfType, NAME_PATTERN, "UDF Type");
        this.udfType = udfType == null ? null : StringEscapeUtils.escapeHtml4(udfType.trim());
    }
}

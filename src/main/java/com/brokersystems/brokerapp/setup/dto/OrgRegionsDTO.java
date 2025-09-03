package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrgRegionsDTO {

    private static final Logger logger = LoggerFactory.getLogger(OrgRegionsDTO.class);

    private Long regCode;
    private String shtDesc;
    private String regDesc;

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


    public void setShtDesc(String shtDesc) {
        validateAndSanitize(shtDesc, DESC_PATTERN, "Short Description");
        this.shtDesc = shtDesc == null ? null : StringEscapeUtils.escapeHtml4(shtDesc.trim());
    }

    public void setRegDesc(String regDesc) {
        validateAndSanitize(regDesc, DESC_PATTERN, "Region Description");
        this.regDesc = regDesc == null ? null : StringEscapeUtils.escapeHtml4(regDesc.trim());
    }
}

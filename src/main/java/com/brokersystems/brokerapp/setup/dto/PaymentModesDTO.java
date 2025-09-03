package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

public class PaymentModesDTO {

    private static final Logger logger = LoggerFactory.getLogger(PaymentModesDTO.class);

    private Long pmId;
    private String pmDesc;

    public Long getPmId() {
        return pmId;
    }

    public void setPmId(Long pmId) {
        this.pmId = pmId;
    }

    public String getPmDesc() {
        return pmDesc;
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

    public void setPmDesc(String pmDesc) {
        validateAndSanitize(pmDesc, DESC_PATTERN, "Payment Mode Description");
        this.pmDesc = pmDesc == null ? null : StringEscapeUtils.escapeHtml4(pmDesc.trim());
    }

}

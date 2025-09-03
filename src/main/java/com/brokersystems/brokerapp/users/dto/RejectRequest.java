package com.brokersystems.brokerapp.users.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Data
public class RejectRequest {

    private static final Logger logger = LoggerFactory.getLogger(RejectRequest.class);

    private Long taskId;
    private Long reasonId;
    private String reason;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s", fieldName)
            );
        }
    }

    public void setReason(String reason) {
        validateAndSanitize(reason, DESC_PATTERN, "Reason");
        this.reason = reason == null ? null : StringEscapeUtils.escapeHtml4(reason.trim());
    }
}

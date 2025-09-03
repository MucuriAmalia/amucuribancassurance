package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

public class RejectRequistionForm {

    private static final Logger logger = LoggerFactory.getLogger(RejectRequistionForm.class);

    private Long reqId;
    private String reason;

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public String getReason() {
        return reason;
    }


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

    public void setReason(String reason) {
        validateAndSanitize(reason, DESC_PATTERN, "Reason");
        this.reason = reason == null ? null : StringEscapeUtils.escapeHtml4(reason.trim());
    }

}

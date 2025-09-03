package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqDocsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ReqDocsDTO.class);

    private Long sclReqrdId;
    private String reqShtDesc;
    private String reqDesc;

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

    public void setReqShtDesc(String reqShtDesc) {
        validateAndSanitize(reqShtDesc, DESC_PATTERN, "Request Short Description");
        this.reqShtDesc = reqShtDesc == null ? null : StringEscapeUtils.escapeHtml4(reqShtDesc.trim());
    }

    public void setReqDesc(String reqDesc) {
        validateAndSanitize(reqDesc, DESC_PATTERN, "Request Description");
        this.reqDesc = reqDesc == null ? null : StringEscapeUtils.escapeHtml4(reqDesc.trim());
    }
}

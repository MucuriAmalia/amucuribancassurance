package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;


@Getter
public class QuoteClauseDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuoteClauseDTO.class);

    @Setter
    private Long qpClauId;
    @Setter
    private Long clauseId;
    @Setter
    private Long quoteProductId;
    private String clauHeading;
    private String clauShtDesc;
    private String clauseType;
    private String editable;
    private String clauWording;
    private String quotStatus;

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


    public void setClauHeading(String clauHeading) {
        validateAndSanitize(clauHeading, DESC_PATTERN, "Clause Heading");
        this.clauHeading = clauHeading == null ? null : StringEscapeUtils.escapeHtml4(clauHeading.trim());
    }

    public void setClauShtDesc(String clauShtDesc) {
        validateAndSanitize(clauShtDesc, DESC_PATTERN, "Clause Short Description");
        this.clauShtDesc = clauShtDesc == null ? null : StringEscapeUtils.escapeHtml4(clauShtDesc.trim());
    }

    public void setClauseType(String clauseType) {
        validateAndSanitize(clauseType, DESC_PATTERN, "Clause Type");
        this.clauseType = clauseType == null ? null : StringEscapeUtils.escapeHtml4(clauseType.trim());
    }

    public void setEditable(String editable) {
        validateAndSanitize(editable, DESC_PATTERN, "Editable");
        this.editable = editable == null ? null : StringEscapeUtils.escapeHtml4(editable.trim());
    }

    public void setClauWording(String clauWording) {
        validateAndSanitize(clauWording, DESC_PATTERN, "Clause Wording");
        this.clauWording = clauWording == null ? null : StringEscapeUtils.escapeHtml4(clauWording.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, DESC_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }
}

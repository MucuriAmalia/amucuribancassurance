// Create these DTO classes in your com.brokersystems.brokerapp.setup.dto package

package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * DTO for subclass validation results
 */
public class SubclassMappingValidation {

    private static final Logger logger = LoggerFactory.getLogger(SubclassMappingValidation.class);

    private Long subId;
    private String subShtDesc;
    private String subDesc;
    private boolean hasRevenueMapping;
    private Long revenueItemsCount;
    private String validationMessage;

    // Default constructor
    public SubclassMappingValidation() {}

    // Constructor with basic fields
    public SubclassMappingValidation(Long subId, String subShtDesc, String subDesc, boolean hasRevenueMapping) {
        this.subId = subId;
        setSubDesc(subDesc);
        setSubShtDesc(subShtDesc);
        this.hasRevenueMapping = hasRevenueMapping;
    }

    // Getters and setters
    public Long getSubId() { return subId; }
    public void setSubId(Long subId) { this.subId = subId; }

    public String getSubShtDesc() { return subShtDesc; }

    public String getSubDesc() { return subDesc; }

    public boolean isHasRevenueMapping() { return hasRevenueMapping; }
    public void setHasRevenueMapping(boolean hasRevenueMapping) { this.hasRevenueMapping = hasRevenueMapping; }

    public Long getRevenueItemsCount() { return revenueItemsCount; }
    public void setRevenueItemsCount(Long revenueItemsCount) { this.revenueItemsCount = revenueItemsCount; }

    public String getValidationMessage() { return validationMessage; }

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

    public void setSubShtDesc(String subShtDesc) {
        validateAndSanitize(subShtDesc, DESC_PATTERN, "Subclass Short Description");
        this.subShtDesc = subShtDesc == null ? null : StringEscapeUtils.escapeHtml4(subShtDesc.trim());
    }

    public void setSubDesc(String subDesc) {
        validateAndSanitize(subDesc, DESC_PATTERN, "Subclass Description");
        this.subDesc = subDesc == null ? null : StringEscapeUtils.escapeHtml4(subDesc.trim());
    }

    public void setValidationMessage(String validationMessage) {
        validateAndSanitize(validationMessage, DESC_PATTERN, "Validation Message");
        this.validationMessage = validationMessage == null ? null : StringEscapeUtils.escapeHtml4(validationMessage.trim());
    }

}


package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * DTO for subclass with mapping status
 */
public class SubclassWithMappingStatus {

    private static final Logger logger = LoggerFactory.getLogger(SubclassWithMappingStatus.class);

    private Long subId;
    private String subShtDesc;
    private String subDesc;
    private boolean active;
    private boolean hasRevenueMapping;
    private int revenueItemCount;

    // Default constructor
    public SubclassWithMappingStatus() {}

    // Getters and setters
    public Long getSubId() { return subId; }
    public void setSubId(Long subId) { this.subId = subId; }

    public String getSubShtDesc() { return subShtDesc; }

    public String getSubDesc() { return subDesc; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public boolean isHasRevenueMapping() { return hasRevenueMapping; }
    public void setHasRevenueMapping(boolean hasRevenueMapping) { this.hasRevenueMapping = hasRevenueMapping; }

    public int getRevenueItemCount() { return revenueItemCount; }
    public void setRevenueItemCount(int revenueItemCount) { this.revenueItemCount = revenueItemCount; }

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
}

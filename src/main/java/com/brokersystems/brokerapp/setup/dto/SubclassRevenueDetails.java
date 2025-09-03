package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

/**
 * DTO for detailed subclass revenue information
 */
public class SubclassRevenueDetails {

    private static final Logger logger = LoggerFactory.getLogger(SubclassRevenueDetails.class);

    private Long subId;
    private String subShtDesc;
    private String subDesc;
    private List<RevenueItemMappingInfo> revenueItems;
    private boolean hasValidMappings;

    // Default constructor
    public SubclassRevenueDetails() {
        this.revenueItems = new ArrayList<>();
    }

    // Getters and setters
    public Long getSubId() { return subId; }
    public void setSubId(Long subId) { this.subId = subId; }

    public String getSubShtDesc() { return subShtDesc; }

    public String getSubDesc() { return subDesc; }

    public List<RevenueItemMappingInfo> getRevenueItems() { return revenueItems; }
    public void setRevenueItems(List<RevenueItemMappingInfo> revenueItems) { this.revenueItems = revenueItems; }

    public boolean isHasValidMappings() { return hasValidMappings; }
    public void setHasValidMappings(boolean hasValidMappings) { this.hasValidMappings = hasValidMappings; }

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

package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

/**
 * DTO for revenue item mapping information
 */
public class RevenueItemMappingInfo {

    private static final Logger logger = LoggerFactory.getLogger(RevenueItemMappingInfo.class);

    private String item;
    private String drAccountCode;
    private String drAccountName;
    private String crAccountCode;
    private String crAccountName;
    private boolean hasValidMapping;

    // Default constructor
    public RevenueItemMappingInfo() {}

    // Getters and setters
    public String getItem() { return item; }


    public String getDrAccountCode() { return drAccountCode; }


    public String getDrAccountName() { return drAccountName; }


    public String getCrAccountCode() { return crAccountCode; }


    public String getCrAccountName() { return crAccountName; }


    public boolean isHasValidMapping() { return hasValidMapping; }

    public void setHasValidMapping(boolean hasValidMapping) { this.hasValidMapping = hasValidMapping; }

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

    public void setItem(String item) {
        validateAndSanitize(item, NAME_PATTERN, "Item");
        this.item = item == null ? null : StringEscapeUtils.escapeHtml4(item.trim());
    }

    public void setDrAccountCode(String drAccountCode) {
        validateAndSanitize(drAccountCode, REF_PATTERN, "Debit Account Code");
        this.drAccountCode = drAccountCode == null ? null : StringEscapeUtils.escapeHtml4(drAccountCode.trim());
    }

    public void setDrAccountName(String drAccountName) {
        validateAndSanitize(drAccountName, NAME_PATTERN, "Debit Account Name");
        this.drAccountName = drAccountName == null ? null : StringEscapeUtils.escapeHtml4(drAccountName.trim());
    }

    public void setCrAccountCode(String crAccountCode) {
        validateAndSanitize(crAccountCode, REF_PATTERN, "Credit Account Code");
        this.crAccountCode = crAccountCode == null ? null : StringEscapeUtils.escapeHtml4(crAccountCode.trim());
    }

    public void setCrAccountName(String crAccountName) {
        validateAndSanitize(crAccountName, NAME_PATTERN, "Credit Account Name");
        this.crAccountName = crAccountName == null ? null : StringEscapeUtils.escapeHtml4(crAccountName.trim());
    }

}

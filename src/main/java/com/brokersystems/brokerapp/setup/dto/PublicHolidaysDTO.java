package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

public class PublicHolidaysDTO {

    private static final Logger logger = LoggerFactory.getLogger(PublicHolidaysDTO.class);

    private String holidayName;
    private String holidayDate; // As string in yyyy-MM-dd format
    private String isRecurring;

    // Default constructor
    public PublicHolidaysDTO() {
    }

    // Parameterized constructor
    public PublicHolidaysDTO(String holidayName, String holidayDate, String isRecurring) {
        this.holidayName = holidayName;
        this.holidayDate = holidayDate;
        this.isRecurring = isRecurring;
    }

    // Getters
    public String getHolidayName() {
        return holidayName;
    }

    public String getHolidayDate() {
        return holidayDate;
    }

    public String getIsRecurring() {
        return isRecurring;
    }


    // toString() for debugging
    @Override
    public String toString() {
        return "PublicHolidaysDTO{" +
                "holidayName='" + holidayName + '\'' +
                ", holidayDate='" + holidayDate + '\'' +
                ", isRecurring='" + isRecurring + '\'' +
                '}';
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

    public void setHolidayName(String holidayName) {
        validateAndSanitize(holidayName, NAME_PATTERN, "Holiday Name");
        this.holidayName = holidayName == null ? null : StringEscapeUtils.escapeHtml4(holidayName.trim());
    }

    public void setHolidayDate(String holidayDate) {
        validateAndSanitize(holidayDate, REF_PATTERN, "Holiday Date");
        this.holidayDate = holidayDate == null ? null : StringEscapeUtils.escapeHtml4(holidayDate.trim());
    }

    public void setIsRecurring(String isRecurring) {
        validateAndSanitize(isRecurring, NAME_PATTERN, "Is Recurring");
        this.isRecurring = isRecurring == null ? null : StringEscapeUtils.escapeHtml4(isRecurring.trim());
    }
}
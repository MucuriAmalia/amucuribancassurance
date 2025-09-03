package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.FILENAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

public class PremRatesDTO {

    private static final Logger logger = LoggerFactory.getLogger(PremRatesDTO.class);

    private Long id;
    private String rate_table;
    private Date effDate;
    private String fileName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRate_table() {
        return rate_table;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getFileName() {
        return fileName;
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

    public void setRate_table(String rate_table) {
        validateAndSanitize(rate_table, REF_PATTERN, "Rate Table");
        this.rate_table = rate_table == null ? null : StringEscapeUtils.escapeHtml4(rate_table.trim());
    }

    public void setFileName(String fileName) {
        validateAndSanitize(fileName, FILENAME_PATTERN, "File Name");
        this.fileName = fileName == null ? null : StringEscapeUtils.escapeHtml4(fileName.trim());
    }
}

package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

public class ExchangeRatesDTO {

    private static final Logger logger = LoggerFactory.getLogger(ExchangeRatesDTO.class);

    private boolean success;
    private int timestamp;
    private String base;
    private String date;
    private RatesDTO rates;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public RatesDTO getRates() {
        return rates;
    }

    public void setRates(RatesDTO rates) {
        this.rates = rates;
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


    public void setBase(String base) {
        validateAndSanitize(base, NAME_PATTERN, "Base Currency");
        this.base = base == null ? null : StringEscapeUtils.escapeHtml4(base.trim());
    }

    public void setDate(String date) {
        validateAndSanitize(date, REF_PATTERN, "Date");
        this.date = date == null ? null : StringEscapeUtils.escapeHtml4(date.trim());
    }
}

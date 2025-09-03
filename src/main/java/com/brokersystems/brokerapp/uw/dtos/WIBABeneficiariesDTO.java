package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

public class WIBABeneficiariesDTO {

    private static final Logger logger = LoggerFactory.getLogger(WIBABeneficiariesDTO.class);

    private Long bwbId;
    private String fullName;
    private String occupation;
    private BigDecimal salary;
    private Long riskId;

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public Long getBwbId() {
        return bwbId;
    }

    public void setBwbId(Long bwbId) {
        this.bwbId = bwbId;
    }

    public String getFullName() {
        return fullName;
    }


    public String getOccupation() {
        return occupation;
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

    public void setFullName(String fullName) {
        validateAndSanitize(fullName, NAME_PATTERN, "Full Name");
        this.fullName = fullName == null ? null : StringEscapeUtils.escapeHtml4(fullName.trim());
    }

    public void setOccupation(String occupation) {
        validateAndSanitize(occupation, NAME_PATTERN, "Occupation");
        this.occupation = occupation == null ? null : StringEscapeUtils.escapeHtml4(occupation.trim());
    }
}

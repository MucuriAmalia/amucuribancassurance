package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveRisksDTO {

    private static final Logger logger = LoggerFactory.getLogger(ActiveRisksDTO.class);


    private Long riskCode;
    private String riskStatus;
    private Long polId;
    private Long prevRiskId;
    private Long riskId;

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

    public void setRiskStatus(String riskStatus) {
        validateAndSanitize(riskStatus, NAME_PATTERN, "Risk Status");
        this.riskStatus = riskStatus == null ? null : StringEscapeUtils.escapeHtml4(riskStatus.trim());
    }


}

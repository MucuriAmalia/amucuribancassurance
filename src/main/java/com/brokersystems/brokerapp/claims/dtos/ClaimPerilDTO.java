package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;


@Getter
@Slf4j
public class ClaimPerilDTO {

    @Setter
    private Long bindPerilCode;

    private String perilDesc;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Peril Description" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Peril Description", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.DESC_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Peril Description");
        }
    }

    public void setPerilDesc(String perilDesc) {
        validateAndSanitize(perilDesc);
        this.perilDesc = perilDesc == null ? null : StringEscapeUtils.escapeHtml4(perilDesc.trim());
    }
}
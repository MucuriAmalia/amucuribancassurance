package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@Slf4j
public class ServiceProviderTypesDTO {

    @Setter
    private Long typeId;

    private String providerType;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Provider Type" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Provider Type", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Provider Type");
        }
    }

    public void setProviderType(String providerType) {
        validateAndSanitize(providerType);
        this.providerType = providerType == null ? null : StringEscapeUtils.escapeHtml4(providerType.trim());
    }
}
package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;

/**
 * Created by peter on 3/6/2017.
 */
@Getter
@Slf4j
public class PerilBean {

    @Setter
    private Long perilCode;

    private String selfAsClaimant;
    @Setter
    private BigDecimal perilEstimate;

    @Setter
    private Long claimId;
    @Setter
    private Long claimantCode;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Self As Claimant" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Self As Claimant", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Self As Claimant");
        }
    }

    public void setSelfAsClaimant(String selfAsClaimant) {
        validateAndSanitize(selfAsClaimant);
        this.selfAsClaimant = selfAsClaimant == null ? null : StringEscapeUtils.escapeHtml4(selfAsClaimant.trim());
    }

}
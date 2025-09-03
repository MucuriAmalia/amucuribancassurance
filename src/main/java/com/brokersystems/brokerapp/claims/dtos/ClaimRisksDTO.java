package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import static com.brokersystems.brokerapp.common.Constants.*;

@Slf4j
@Getter
public class ClaimRisksDTO {

    private final Long riskId;
    private final String riskShtDesc;
    private final String polno;
    private final String clientname;
    private final Long binderId;
    private final Long binderDetId;
    private final Long polId;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            log.warn("Validation failed for field '{}'. Input contains invalid characters. Input: '{}'",
                    fieldName, input);
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    private String sanitize(String input, String fieldName) {
        if (input == null) {
            log.debug("Field '{}' is null, no sanitization needed", fieldName);
            return null;
        }

        String trimmed = input.trim();
        if (trimmed.isEmpty()) {
            log.debug("Field '{}' is empty after trimming, no sanitization needed", fieldName);
            return trimmed;
        }

        String sanitized = StringEscapeUtils.escapeHtml4(trimmed);

        // Only log if sanitization actually changed the input
        if (!trimmed.equals(sanitized)) {
            log.info("Sanitizing field '{}'. Original: '{}', Sanitized: '{}'",
                    fieldName, trimmed, sanitized);
        } else {
            log.debug("Field '{}' required no HTML escaping. Value: '{}'", fieldName, trimmed);
        }

        return sanitized;
    }

    private ClaimRisksDTO(Long riskId, String riskShtDesc, String polno, String clientname, Long binderId, Long binderDetId, Long polId) {
        log.debug("Creating ClaimRisksDTO with riskId: {}, binderId: {}, binderDetId: {}, polId: {}",
                riskId, binderId, binderDetId, polId);

        // Log input values before validation and sanitization
        log.debug("Input values - riskShtDesc: '{}', polno: '{}', clientname: '{}'",
                riskShtDesc, polno, clientname);

        // Validate inputs
        validateAndSanitize(riskShtDesc, DESC_PATTERN, "Risk Short Description");
        validateAndSanitize(polno, REF_PATTERN, "Policy Number");
        validateAndSanitize(clientname, NAME_PATTERN, "Client Name");

        // Assign non-string fields
        this.riskId = riskId;
        this.binderId = binderId;
        this.binderDetId = binderDetId;
        this.polId = polId;

        // Sanitize and assign string fields
        this.riskShtDesc = sanitize(riskShtDesc, "Risk Short Description");
        this.polno = sanitize(polno, "Policy Number");
        this.clientname = sanitize(clientname, "Client Name");

        log.debug("ClaimRisksDTO created successfully with sanitized values");
    }

    public static ClaimRisksDTO instance(Long riskId, String riskShtDesc, String polno, String clientname, Long binderId, Long binderDetId, Long polId) {
        log.debug("Creating ClaimRisksDTO instance via factory method");
        return new ClaimRisksDTO(riskId, riskShtDesc, polno, clientname, binderId, binderDetId, polId);
    }
}
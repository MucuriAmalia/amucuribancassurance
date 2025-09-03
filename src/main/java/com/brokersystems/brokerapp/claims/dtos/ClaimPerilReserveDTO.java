package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Getter
@Slf4j
public class ClaimPerilReserveDTO {

    private String perilDesc;

    private String type;

    @Setter
    private BigDecimal limitAmt;

    @Setter
    private BigDecimal excessAmt;

    @Setter
    private BigDecimal reserve;

    private String remarks;

    @Setter
    private Long clmPerilId;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPerilDesc(String perilDesc) {
        validateAndSanitize(perilDesc, DESC_PATTERN, "Peril Description");
        this.perilDesc = perilDesc == null ? null : StringEscapeUtils.escapeHtml4(perilDesc.trim());
    }

    public void setType(String type) {
        validateAndSanitize(type, NAME_PATTERN, "Type");
        this.type = type == null ? null : StringEscapeUtils.escapeHtml4(type.trim());
    }

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }
}
package com.brokersystems.brokerapp.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminFeeDTO {

    private static final Logger logger = LoggerFactory.getLogger(AdminFeeDTO.class);

    private Long adminId;
    private Long binderId;
    private BigDecimal vatRate;
    private String vateRateType;
    private BigDecimal exciseRate;
    private String exciseRateType;
    private boolean binStatus;
    private String status;
    private BigDecimal adminFeeRate;
    private String adminFeeRateType;

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

    public void setVateRateType(String vateRateType) {
        validateAndSanitize(vateRateType, REF_PATTERN, "VAT Rate Type");
        this.vateRateType = vateRateType == null ? null : StringEscapeUtils.escapeHtml4(vateRateType.trim());
    }

    public void setExciseRateType(String exciseRateType) {
        validateAndSanitize(exciseRateType, REF_PATTERN, "Excise Rate Type");
        this.exciseRateType = exciseRateType == null ? null : StringEscapeUtils.escapeHtml4(exciseRateType.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setAdminFeeRateType(String adminFeeRateType) {
        validateAndSanitize(adminFeeRateType, REF_PATTERN, "Admin Fee Rate Type");
        this.adminFeeRateType = adminFeeRateType == null ? null : StringEscapeUtils.escapeHtml4(adminFeeRateType.trim());
    }

}

package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RiskBatchDTO {

    private static final Logger logger = LoggerFactory.getLogger(RiskBatchDTO.class);

    private Long riskId;
    private BigDecimal riskPrem;
    private BigDecimal calcPrem;
    private BigDecimal riskInsured;
    private BigDecimal installAmt;
    private Date wetDate;
    private BigDecimal totalPercent;
    private BigDecimal comm;
    private BigDecimal subAgentComm;
    private BigDecimal marketerAgentComm;
    private BigDecimal introducerAgentComm;
    private BigDecimal netPremium;
    private BigDecimal riskextras;
    private BigDecimal riskphfFund;
    private BigDecimal riskTl;
    private BigDecimal riskstampDuty;
    private BigDecimal riskFuturePrem;
    private String installmentPercentage;

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

    public void setInstallmentPercentage(String installmentPercentage) {
        validateAndSanitize(installmentPercentage, REF_PATTERN, "Installment Percentage");
        this.installmentPercentage = installmentPercentage == null ? null : StringEscapeUtils.escapeHtml4(installmentPercentage.trim());
    }
}

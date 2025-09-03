package com.brokersystems.brokerapp.life.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptAllocationCommissionsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptAllocationCommissionsDTO.class);

    private Long allocCommId;
    private Integer installNo;
    private BigDecimal instalmentPremium;
    private BigDecimal commissionAmt;
    private BigDecimal subAgentCommissionAmt;
    private BigDecimal marketerCommissionAmt;
    private String paidToDate;
    private String premiumItem;


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        if (!input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPaidToDate(String paidToDate) {
        validateAndSanitize(paidToDate, DESC_PATTERN, "Paid To Date");
        this.paidToDate = paidToDate == null ? null : StringEscapeUtils.escapeHtml4(paidToDate.trim());
    }

    public void setPremiumItem(String premiumItem) {
        validateAndSanitize(premiumItem, NAME_PATTERN, "Premium Item");
        this.premiumItem = premiumItem == null ? null : StringEscapeUtils.escapeHtml4(premiumItem.trim());
    }

}

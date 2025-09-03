package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
public class LifeReceiptsDTO {

    private static final Logger logger = LoggerFactory.getLogger(LifeReceiptsDTO.class);

    private String receiptNo;
    private Date receiptDate;
    private String dc;
    private BigDecimal receiptAmount;
    private BigDecimal allocationAmount;
    private BigDecimal balance;
    private Long receiptId;
    private Long lifeRctId;
    private BigDecimal commissionAmount;
    private BigDecimal subAgentcommissionAmount;
    private BigDecimal marketerCommissionAmount;
    private BigDecimal whtxAmt;

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

    public void setReceiptNo(String receiptNo) {
        validateAndSanitize(receiptNo, REF_PATTERN, "Receipt Number");
        this.receiptNo = receiptNo == null ? null : StringEscapeUtils.escapeHtml4(receiptNo.trim());
    }

    public void setDc(String dc) {
        validateAndSanitize(dc, REF_PATTERN, "Debit/Credit Indicator");
        this.dc = dc == null ? null : StringEscapeUtils.escapeHtml4(dc.trim());
    }

}

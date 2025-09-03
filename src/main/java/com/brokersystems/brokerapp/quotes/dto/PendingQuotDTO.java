package com.brokersystems.brokerapp.quotes.dto;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
public class PendingQuotDTO {

    private static final Logger logger = LoggerFactory.getLogger(PendingQuotDTO.class);

    private String quotNo;
    @Setter
    private BigDecimal premium;
    @Setter
    private Date expiryDate;
    private String status;
    @Setter
    private Date quotDate;
    private String product;
    @Setter
    private Long quoteId;

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

    public void setQuotNo(String quotNo) {
        validateAndSanitize(quotNo, REF_PATTERN, "Quote Number");
        this.quotNo = quotNo == null ? null : StringEscapeUtils.escapeHtml4(quotNo.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, REF_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }
}

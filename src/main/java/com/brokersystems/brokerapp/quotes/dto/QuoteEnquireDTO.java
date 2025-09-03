package com.brokersystems.brokerapp.quotes.dto;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
public class QuoteEnquireDTO {

    private static final Logger logger = LoggerFactory.getLogger(QuoteEnquireDTO.class);

    @Setter
    private Long quoteId;
    private String quotNo;
    private String quotRevNo;
    @Setter
    private Date wefDate;
    @Setter
    private Date wetDate;
    private String client;
    private String product;
    private String curIsoCode;
    private String quotStatus;
    private String username;

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

    public void setQuotRevNo(String quotRevNo) {
        validateAndSanitize(quotRevNo, REF_PATTERN, "Quote Revision Number");
        this.quotRevNo = quotRevNo == null ? null : StringEscapeUtils.escapeHtml4(quotRevNo.trim());
    }

    public void setClient(String client) {
        validateAndSanitize(client, NAME_PATTERN, "Client");
        this.client = client == null ? null : StringEscapeUtils.escapeHtml4(client.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }

    public void setCurIsoCode(String curIsoCode) {
        validateAndSanitize(curIsoCode, REF_PATTERN, "Currency ISO Code");
        this.curIsoCode = curIsoCode == null ? null : StringEscapeUtils.escapeHtml4(curIsoCode.trim());
    }

    public void setQuotStatus(String quotStatus) {
        validateAndSanitize(quotStatus, REF_PATTERN, "Quote Status");
        this.quotStatus = quotStatus == null ? null : StringEscapeUtils.escapeHtml4(quotStatus.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }
}

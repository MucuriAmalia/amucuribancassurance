package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientTransactionDTO {

    private static final Logger logger = LoggerFactory.getLogger(ClientTransactionDTO.class);

    private String policyNo;
    private String transactionRef;
    private String transactionType;
    private BigDecimal grossAmount;
    private BigDecimal netAmount;
    private BigDecimal transactionBalance;
    private Date transDate;
    private Date wefDate;
    private Date wetDate;
    private String product;


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

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setTransactionRef(String transactionRef) {
        validateAndSanitize(transactionRef, REF_PATTERN, "Transaction Reference");
        this.transactionRef = transactionRef == null ? null : StringEscapeUtils.escapeHtml4(transactionRef.trim());
    }

    public void setTransactionType(String transactionType) {
        validateAndSanitize(transactionType, REF_PATTERN, "Transaction Type");
        this.transactionType = transactionType == null ? null : StringEscapeUtils.escapeHtml4(transactionType.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }



}

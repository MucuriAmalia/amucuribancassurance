package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class productCodesDTO {

    private static final Logger logger = LoggerFactory.getLogger(productCodesDTO.class);

    private Long prodId;
    private String productCode;
    private String productDescription;

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

    public void setProductCode(String productCode) {
        validateAndSanitize(productCode, REF_PATTERN, "Product Code");
        this.productCode = productCode == null ? null : StringEscapeUtils.escapeHtml4(productCode.trim());
    }

    public void setProductDescription(String productDescription) {
        validateAndSanitize(productDescription, DESC_PATTERN, "Product Description");
        this.productDescription = productDescription == null ? null : StringEscapeUtils.escapeHtml4(productDescription.trim());
    }

}

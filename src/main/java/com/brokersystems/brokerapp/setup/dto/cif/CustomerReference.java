package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerReference {

    private static final Logger logger = LoggerFactory.getLogger(CustomerReference.class);

    private String customerSegment;
    private String customerType;

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

    public void setCustomerSegment(String customerSegment) {
        validateAndSanitize(customerSegment, REF_PATTERN, "Customer Segment");
        this.customerSegment = customerSegment == null ? null : StringEscapeUtils.escapeHtml4(customerSegment.trim());
    }

    public void setCustomerType(String customerType) {
        validateAndSanitize(customerType, REF_PATTERN, "Customer Type");
        this.customerType = customerType == null ? null : StringEscapeUtils.escapeHtml4(customerType.trim());
    }

}

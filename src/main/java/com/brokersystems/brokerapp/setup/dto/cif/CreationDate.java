package com.brokersystems.brokerapp.setup.dto.cif;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreationDate {

    private static final Logger logger = LoggerFactory.getLogger(CreationDate.class);

    private String customerCreateDate;

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

    public void setCustomerCreateDate(String customerCreateDate) {
        validateAndSanitize(customerCreateDate, DESC_PATTERN, "Customer Create Date");
        this.customerCreateDate = customerCreateDate == null ? null : StringEscapeUtils.escapeHtml4(customerCreateDate.trim());
    }

}

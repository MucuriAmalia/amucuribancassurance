package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.ID_NUMBER_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeReference {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeReference.class);

    private String bankStaffNumber;
    private String staffFlag;

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


    public void setBankStaffNumber(String bankStaffNumber) {
        validateAndSanitize(bankStaffNumber, ID_NUMBER_PATTERN,"Bank Staff Number");
        this.bankStaffNumber = bankStaffNumber == null ? null : StringEscapeUtils.escapeHtml4(bankStaffNumber.trim());
    }

    public void setStaffFlag(String staffFlag) {
        validateAndSanitize(staffFlag, NAME_PATTERN, "Staff Flag");
        this.staffFlag = staffFlag == null ? null : StringEscapeUtils.escapeHtml4(staffFlag.trim());
    }
}

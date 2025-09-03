package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountTypesDTO {

    private static final Logger logger = LoggerFactory.getLogger(AccountTypesDTO.class);

    private Long accId;
    private String accName;
    private String accountType;

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

    public void setAccName(String accName) {
        validateAndSanitize(accName, NAME_PATTERN, "Account Name");
        this.accName = accName == null ? null : StringEscapeUtils.escapeHtml4(accName.trim());
    }

    public void setAccountType(String accountType) {
        validateAndSanitize(accountType, REF_PATTERN, "Account Type");
        this.accountType = accountType == null ? null : StringEscapeUtils.escapeHtml4(accountType.trim());
    }

}

package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Getter
@Setter
public class PolicyMiscInfoDTO {

    private static final Logger logger = LoggerFactory.getLogger(PolicyMiscInfoDTO.class);

    private String accountNumber;
    private String accountName;
    private String strikeDay;
    private String accountType;
    private String bankId;
    private String branchId;
    private Double inflationPercent;
    private  Long polId;

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

    public void setAccountNumber(String accountNumber) {
        validateAndSanitize(accountNumber, REF_PATTERN, "Account Number");
        this.accountNumber = accountNumber == null ? null : StringEscapeUtils.escapeHtml4(accountNumber.trim());
    }

    public void setAccountName(String accountName) {
        validateAndSanitize(accountName, NAME_PATTERN, "Account Name");
        this.accountName = accountName == null ? null : StringEscapeUtils.escapeHtml4(accountName.trim());
    }

    public void setStrikeDay(String strikeDay) {
        validateAndSanitize(strikeDay, NAME_PATTERN, "Strike Day");
        this.strikeDay = strikeDay == null ? null : StringEscapeUtils.escapeHtml4(strikeDay.trim());
    }

    public void setAccountType(String accountType) {
        validateAndSanitize(accountType, NAME_PATTERN, "Account Type");
        this.accountType = accountType == null ? null : StringEscapeUtils.escapeHtml4(accountType.trim());
    }

    public void setBankId(String bankId) {
        validateAndSanitize(bankId, REF_PATTERN, "Bank ID");
        this.bankId = bankId == null ? null : StringEscapeUtils.escapeHtml4(bankId.trim());
    }

    public void setBranchId(String branchId) {
        validateAndSanitize(branchId, REF_PATTERN, "Branch ID");
        this.branchId = branchId == null ? null : StringEscapeUtils.escapeHtml4(branchId.trim());
    }
}

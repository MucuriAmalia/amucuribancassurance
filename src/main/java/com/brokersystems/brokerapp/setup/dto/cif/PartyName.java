package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PartyName {

    private static final Logger logger = LoggerFactory.getLogger(PartyName.class);

    private String aliasName;
    private String firstName;
    private String fullName;
    private String lastName;
    private String middleName;

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

    public void setAliasName(String aliasName) {
        validateAndSanitize(aliasName, NAME_PATTERN, "Alias Name");
        this.aliasName = aliasName == null ? null : StringEscapeUtils.escapeHtml4(aliasName.trim());
    }

    public void setFirstName(String firstName) {
        validateAndSanitize(firstName, NAME_PATTERN, "First Name");
        this.firstName = firstName == null ? null : StringEscapeUtils.escapeHtml4(firstName.trim());
    }

    public void setFullName(String fullName) {
        validateAndSanitize(fullName, NAME_PATTERN, "Full Name");
        this.fullName = fullName == null ? null : StringEscapeUtils.escapeHtml4(fullName.trim());
    }

    public void setLastName(String lastName) {
        validateAndSanitize(lastName, NAME_PATTERN, "Last Name");
        this.lastName = lastName == null ? null : StringEscapeUtils.escapeHtml4(lastName.trim());
    }

    public void setMiddleName(String middleName) {
        validateAndSanitize(middleName, NAME_PATTERN, "Middle Name");
        this.middleName = middleName == null ? null : StringEscapeUtils.escapeHtml4(middleName.trim());
    }

}

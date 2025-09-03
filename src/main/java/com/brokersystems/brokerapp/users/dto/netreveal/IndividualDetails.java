package com.brokersystems.brokerapp.users.dto.netreveal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.ID_NUMBER_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static net.sf.jasperreports.types.date.FixedDate.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IndividualDetails {

    private static final Logger logger = LoggerFactory.getLogger(IndividualDetails.class);

    private String firstName;
    private String middleName;
    private String surName;
    private String identificationNumber;
    private String dateOfBirth;
    private String nationality;
    private String countryOfResidence;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s", fieldName)
            );
        }
    }

    public void setFirstName(String firstName) {
        validateAndSanitize(firstName, NAME_PATTERN, "First Name");
        this.firstName = firstName == null ? null : StringEscapeUtils.escapeHtml4(firstName.trim());
    }

    public void setMiddleName(String middleName) {
        validateAndSanitize(middleName, NAME_PATTERN, "Middle Name");
        this.middleName = middleName == null ? null : StringEscapeUtils.escapeHtml4(middleName.trim());
    }

    public void setSurName(String surName) {
        validateAndSanitize(surName, NAME_PATTERN, "Surname");
        this.surName = surName == null ? null : StringEscapeUtils.escapeHtml4(surName.trim());
    }

    public void setIdentificationNumber(String identificationNumber) {
        validateAndSanitize(identificationNumber, ID_NUMBER_PATTERN, "Identification Number");
        this.identificationNumber = identificationNumber == null ? null : StringEscapeUtils.escapeHtml4(identificationNumber.trim());
    }

    public void setDateOfBirth(String dateOfBirth) {
        validateAndSanitize(dateOfBirth, DATE_PATTERN, "Date of Birth");
        this.dateOfBirth = dateOfBirth == null ? null : StringEscapeUtils.escapeHtml4(dateOfBirth.trim());
    }

    public void setNationality(String nationality) {
        validateAndSanitize(nationality, NAME_PATTERN, "Nationality");
        this.nationality = nationality == null ? null : StringEscapeUtils.escapeHtml4(nationality.trim());
    }

    public void setCountryOfResidence(String countryOfResidence) {
        validateAndSanitize(countryOfResidence, NAME_PATTERN, "Country of Residence");
        this.countryOfResidence = countryOfResidence == null ? null : StringEscapeUtils.escapeHtml4(countryOfResidence.trim());
    }
}

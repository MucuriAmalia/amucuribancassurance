package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.ID_NUMBER_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Data
public class PolicyDependentsInfoDTO {

    private static final Logger logger = LoggerFactory.getLogger(PolicyDependentsInfoDTO.class);

    private  Long id;
    private String depType; //efm, parent, child, spouse, main_member
    private String fullName;
    private String surname;
    private  String initials;
    private Date DOB;
    private BigDecimal SumInsured;
    private BigDecimal MonthlyPremium;
    private BigDecimal AnnualPremium;
    private BigDecimal IncomeBenefitPremium;
    private Long policyId;
    private Boolean stillBorn;
    private Boolean isStudent;
    private String gender;
    private String identificationNumber;
    private String identificationType;

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

    public void setDepType(String depType) {
        validateAndSanitize(depType, NAME_PATTERN, "Dependent Type");
        this.depType = depType == null ? null : StringEscapeUtils.escapeHtml4(depType.trim());
    }

    public void setFullName(String fullName) {
        validateAndSanitize(fullName, NAME_PATTERN, "Full Name");
        this.fullName = fullName == null ? null : StringEscapeUtils.escapeHtml4(fullName.trim());
    }

    public void setSurname(String surname) {
        validateAndSanitize(surname, NAME_PATTERN, "Surname");
        this.surname = surname == null ? null : StringEscapeUtils.escapeHtml4(surname.trim());
    }

    public void setInitials(String initials) {
        validateAndSanitize(initials, NAME_PATTERN, "Initials");
        this.initials = initials == null ? null : StringEscapeUtils.escapeHtml4(initials.trim());
    }

    public void setGender(String gender) {
        validateAndSanitize(gender, NAME_PATTERN, "Gender");
        this.gender = gender == null ? null : StringEscapeUtils.escapeHtml4(gender.trim());
    }

    public void setIdentificationNumber(String identificationNumber) {
        validateAndSanitize(identificationNumber, ID_NUMBER_PATTERN, "Identification Number");
        this.identificationNumber = identificationNumber == null ? null : StringEscapeUtils.escapeHtml4(identificationNumber.trim());
    }

    public void setIdentificationType(String identificationType) {
        validateAndSanitize(identificationType, NAME_PATTERN, "Identification Type");
        this.identificationType = identificationType == null ? null : StringEscapeUtils.escapeHtml4(identificationType.trim());
    }
}

package com.brokersystems.brokerapp.setup.dto.cif;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CIFCreationRequest {
    private static final Logger logger = LoggerFactory.getLogger(CIFCreationRequest.class);

    private BirthAddress birthAddress;
    private CustomerReference customerReference;
    private CreationDate date;
    private String dateOfBirth;
    private DocumentDirectoryEntryInstanceReference documentDirectoryEntryInstanceReference;
    private EducationHistory educationHistory;
    private List<EmailAddress> emailAddress;
    private EmployeeReference employeeReference;
    private Employment employment;
    private String ethnicity;
    private String gender;
    private String languageCode;
    private MaritalStatus maritalStatus;
    private String nationality;
    private PartyName partyName;
    private PartyReference partyReference;
    private PoliticalExposureType politicalExposureType;
    private List<PostalAddress> postalAddress;
    private ProductAndServiceReference productAndServiceReference;
    private ProductInstanceReference productInstanceReference;
    private ResidentialAddress residentialAddress;
    private String salutation;
    private List<TelephoneAddress> telephoneAddress;
    private List<UserDefinedFields> userDefinedFields;

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

    public void setDateOfBirth(String dateOfBirth) {
        validateAndSanitize(dateOfBirth, DESC_PATTERN, "Date of Birth");
        this.dateOfBirth = dateOfBirth == null ? null : StringEscapeUtils.escapeHtml4(dateOfBirth.trim());
    }

    public void setEthnicity(String ethnicity) {
        validateAndSanitize(ethnicity, NAME_PATTERN, "Ethnicity");
        this.ethnicity = ethnicity == null ? null : StringEscapeUtils.escapeHtml4(ethnicity.trim());
    }

    public void setGender(String gender) {
        validateAndSanitize(gender, REF_PATTERN, "Gender");
        this.gender = gender == null ? null : StringEscapeUtils.escapeHtml4(gender.trim());
    }

    public void setLanguageCode(String languageCode) {
        validateAndSanitize(languageCode, REF_PATTERN, "Language Code");
        this.languageCode = languageCode == null ? null : StringEscapeUtils.escapeHtml4(languageCode.trim());
    }

    public void setNationality(String nationality) {
        validateAndSanitize(nationality, NAME_PATTERN, "Nationality");
        this.nationality = nationality == null ? null : StringEscapeUtils.escapeHtml4(nationality.trim());
    }

    public void setSalutation(String salutation) {
        validateAndSanitize(salutation, NAME_PATTERN, "Salutation");
        this.salutation = salutation == null ? null : StringEscapeUtils.escapeHtml4(salutation.trim());
    }
}

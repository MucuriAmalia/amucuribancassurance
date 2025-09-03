package com.brokersystems.brokerapp.setup.dto.cif;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employment {

    private static final Logger logger = LoggerFactory.getLogger(Employment.class);

    private String annualIncomeCode;
    private String businessTypeCode;
    private String employerName;
    private String employmentStartDate;
    private int employmentTypeCode;
    private int grossAnnualIncomeAmount;
    private String jobTitleCode;
    private int occupationCode;
    private String postalAddressCityName;
    private String postalAddressCountry;
    private String postalAddressDistrict;
    private String postalAddressLine1;
    private String postalAddressLine2;
    private String postalAddressLine3;
    private String postalAddressPostBoxNumber;
    private String postalAddressStateName;
    private String postalAddressZipCode;
    private String telephoneAddressPhoneNumber;

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

    public void setAnnualIncomeCode(String annualIncomeCode) {
        validateAndSanitize(annualIncomeCode, REF_PATTERN, "Annual Income Code");
        this.annualIncomeCode = annualIncomeCode == null ? null : StringEscapeUtils.escapeHtml4(annualIncomeCode.trim());
    }

    public void setBusinessTypeCode(String businessTypeCode) {
        validateAndSanitize(businessTypeCode, REF_PATTERN, "Business Type Code");
        this.businessTypeCode = businessTypeCode == null ? null : StringEscapeUtils.escapeHtml4(businessTypeCode.trim());
    }

    public void setEmployerName(String employerName) {
        validateAndSanitize(employerName, NAME_PATTERN, "Employer Name");
        this.employerName = employerName == null ? null : StringEscapeUtils.escapeHtml4(employerName.trim());
    }

    public void setEmploymentStartDate(String employmentStartDate) {
        validateAndSanitize(employmentStartDate, DESC_PATTERN, "Employment Start Date");
        this.employmentStartDate = employmentStartDate == null ? null : StringEscapeUtils.escapeHtml4(employmentStartDate.trim());
    }

    public void setJobTitleCode(String jobTitleCode) {
        validateAndSanitize(jobTitleCode, REF_PATTERN, "Job Title Code");
        this.jobTitleCode = jobTitleCode == null ? null : StringEscapeUtils.escapeHtml4(jobTitleCode.trim());
    }

    public void setPostalAddressCityName(String postalAddressCityName) {
        validateAndSanitize(postalAddressCityName, NAME_PATTERN, "Postal Address City Name");
        this.postalAddressCityName = postalAddressCityName == null ? null : StringEscapeUtils.escapeHtml4(postalAddressCityName.trim());
    }

    public void setPostalAddressCountry(String postalAddressCountry) {
        validateAndSanitize(postalAddressCountry, NAME_PATTERN, "Postal Address Country");
        this.postalAddressCountry = postalAddressCountry == null ? null : StringEscapeUtils.escapeHtml4(postalAddressCountry.trim());
    }

    public void setPostalAddressDistrict(String postalAddressDistrict) {
        validateAndSanitize(postalAddressDistrict, NAME_PATTERN, "Postal Address District");
        this.postalAddressDistrict = postalAddressDistrict == null ? null : StringEscapeUtils.escapeHtml4(postalAddressDistrict.trim());
    }

    public void setPostalAddressLine1(String postalAddressLine1) {
        validateAndSanitize(postalAddressLine1, DESC_PATTERN, "Postal Address Line 1");
        this.postalAddressLine1 = postalAddressLine1 == null ? null : StringEscapeUtils.escapeHtml4(postalAddressLine1.trim());
    }

    public void setPostalAddressLine2(String postalAddressLine2) {
        validateAndSanitize(postalAddressLine2, DESC_PATTERN, "Postal Address Line 2");
        this.postalAddressLine2 = postalAddressLine2 == null ? null : StringEscapeUtils.escapeHtml4(postalAddressLine2.trim());
    }

    public void setPostalAddressLine3(String postalAddressLine3) {
        validateAndSanitize(postalAddressLine3, DESC_PATTERN, "Postal Address Line 3");
        this.postalAddressLine3 = postalAddressLine3 == null ? null : StringEscapeUtils.escapeHtml4(postalAddressLine3.trim());
    }

    public void setPostalAddressPostBoxNumber(String postalAddressPostBoxNumber) {
        validateAndSanitize(postalAddressPostBoxNumber, DESC_PATTERN, "Postal Address Post Box Number");
        this.postalAddressPostBoxNumber = postalAddressPostBoxNumber == null ? null : StringEscapeUtils.escapeHtml4(postalAddressPostBoxNumber.trim());
    }

    public void setPostalAddressStateName(String postalAddressStateName) {
        validateAndSanitize(postalAddressStateName, NAME_PATTERN, "Postal Address State Name");
        this.postalAddressStateName = postalAddressStateName == null ? null : StringEscapeUtils.escapeHtml4(postalAddressStateName.trim());
    }

    public void setPostalAddressZipCode(String postalAddressZipCode) {
        validateAndSanitize(postalAddressZipCode, REF_PATTERN, "Postal Address Zip Code");
        this.postalAddressZipCode = postalAddressZipCode == null ? null : StringEscapeUtils.escapeHtml4(postalAddressZipCode.trim());
    }

    public void setTelephoneAddressPhoneNumber(String telephoneAddressPhoneNumber) {
        validateAndSanitize(telephoneAddressPhoneNumber, REF_PATTERN, "Telephone Address Phone Number");
        this.telephoneAddressPhoneNumber = telephoneAddressPhoneNumber == null ? null : StringEscapeUtils.escapeHtml4(telephoneAddressPhoneNumber.trim());
    }
}

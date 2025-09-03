package com.brokersystems.brokerapp.users.dto.netreveal;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;
import static net.sf.jasperreports.types.date.FixedDate.DATE_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EntityDetails {

    private static final Logger logger = LoggerFactory.getLogger(EntityDetails.class);

    private String companyName;
    private String companyTradingAsName;
    private String dateOfRegistration;
    private String companyRegistrationNumber;
    private List<String> countryOfOperation;
    private List<String> countriesTradedWith;
    private String incorporationCountryCode;

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

    public void setCompanyName(String companyName) {
        validateAndSanitize(companyName, NAME_PATTERN, "Company Name");
        this.companyName = companyName == null ? null : StringEscapeUtils.escapeHtml4(companyName.trim());
    }

    public void setCompanyTradingAsName(String companyTradingAsName) {
        validateAndSanitize(companyTradingAsName, NAME_PATTERN, "Company Trading As Name");
        this.companyTradingAsName = companyTradingAsName == null ? null : StringEscapeUtils.escapeHtml4(companyTradingAsName.trim());
    }

    public void setDateOfRegistration(String dateOfRegistration) {
        validateAndSanitize(dateOfRegistration, DATE_PATTERN, "Date of Registration");
        this.dateOfRegistration = dateOfRegistration == null ? null : StringEscapeUtils.escapeHtml4(dateOfRegistration.trim());
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        validateAndSanitize(companyRegistrationNumber, REF_PATTERN, "Company Registration Number");
        this.companyRegistrationNumber = companyRegistrationNumber == null ? null : StringEscapeUtils.escapeHtml4(companyRegistrationNumber.trim());
    }

    public void setIncorporationCountryCode(String incorporationCountryCode) {
        validateAndSanitize(incorporationCountryCode, REF_PATTERN, "Incorporation Country Code");
        this.incorporationCountryCode = incorporationCountryCode == null ? null : StringEscapeUtils.escapeHtml4(incorporationCountryCode.trim());
    }

    public void setCountryOfOperation(List<String> countryOfOperation) {
        if (countryOfOperation != null) {
            this.countryOfOperation = countryOfOperation.stream()
                    .map(country -> {
                        validateAndSanitize(country, NAME_PATTERN, "Country of Operation");
                        return country == null ? null : StringEscapeUtils.escapeHtml4(country.trim());
                    })
                    .collect(Collectors.toList());
        } else {
            this.countryOfOperation = null;
        }
    }

    public void setCountriesTradedWith(List<String> countriesTradedWith) {
        if (countriesTradedWith != null) {
            this.countriesTradedWith = countriesTradedWith.stream()
                    .map(country -> {
                        validateAndSanitize(country, NAME_PATTERN, "Country Traded With");
                        return country == null ? null : StringEscapeUtils.escapeHtml4(country.trim());
                    })
                    .collect(Collectors.toList());
        } else {
            this.countriesTradedWith = null;
        }
    }
}

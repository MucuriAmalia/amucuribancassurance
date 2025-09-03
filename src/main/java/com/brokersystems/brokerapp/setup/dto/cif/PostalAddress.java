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
public class PostalAddress {

    private static final Logger logger = LoggerFactory.getLogger(PostalAddress.class);

    private String addressLine1;
    private String addressLine2;
    private String addressLine3;
    private String addressTypeCode;
    private String cityName;
    private String country;
    private String district;
    private String effectiveAddress;
    private String postBoxNumber;
    private String state;
    private String zipCode;

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

    public void setAddressLine1(String addressLine1) {
        validateAndSanitize(addressLine1, DESC_PATTERN, "Address Line 1");
        this.addressLine1 = addressLine1 == null ? null : StringEscapeUtils.escapeHtml4(addressLine1.trim());
    }

    public void setAddressLine2(String addressLine2) {
        validateAndSanitize(addressLine2, DESC_PATTERN, "Address Line 2");
        this.addressLine2 = addressLine2 == null ? null : StringEscapeUtils.escapeHtml4(addressLine2.trim());
    }

    public void setAddressLine3(String addressLine3) {
        validateAndSanitize(addressLine3, DESC_PATTERN, "Address Line 3");
        this.addressLine3 = addressLine3 == null ? null : StringEscapeUtils.escapeHtml4(addressLine3.trim());
    }

    public void setAddressTypeCode(String addressTypeCode) {
        validateAndSanitize(addressTypeCode, REF_PATTERN, "Address Type Code");
        this.addressTypeCode = addressTypeCode == null ? null : StringEscapeUtils.escapeHtml4(addressTypeCode.trim());
    }

    public void setCityName(String cityName) {
        validateAndSanitize(cityName, NAME_PATTERN, "City Name");
        this.cityName = cityName == null ? null : StringEscapeUtils.escapeHtml4(cityName.trim());
    }

    public void setCountry(String country) {
        validateAndSanitize(country, NAME_PATTERN, "Country");
        this.country = country == null ? null : StringEscapeUtils.escapeHtml4(country.trim());
    }

    public void setDistrict(String district) {
        validateAndSanitize(district, NAME_PATTERN, "District");
        this.district = district == null ? null : StringEscapeUtils.escapeHtml4(district.trim());
    }

    public void setEffectiveAddress(String effectiveAddress) {
        validateAndSanitize(effectiveAddress, DESC_PATTERN, "Effective Address");
        this.effectiveAddress = effectiveAddress == null ? null : StringEscapeUtils.escapeHtml4(effectiveAddress.trim());
    }

    public void setPostBoxNumber(String postBoxNumber) {
        validateAndSanitize(postBoxNumber, REF_PATTERN, "Post Box Number");
        this.postBoxNumber = postBoxNumber == null ? null : StringEscapeUtils.escapeHtml4(postBoxNumber.trim());
    }

    public void setState(String state) {
        validateAndSanitize(state, NAME_PATTERN, "State");
        this.state = state == null ? null : StringEscapeUtils.escapeHtml4(state.trim());
    }

    public void setZipCode(String zipCode) {
        validateAndSanitize(zipCode, REF_PATTERN, "Zip Code");
        this.zipCode = zipCode == null ? null : StringEscapeUtils.escapeHtml4(zipCode.trim());
    }
}

package com.brokersystems.brokerapp.setup.dto.cif;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelephoneAddress {

    private static final Logger logger = LoggerFactory.getLogger(TelephoneAddress.class);

    private String phoneNumber;
    private String telephoneAddressTypeCode;

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

    public void setPhoneNumber(String phoneNumber) {
        validateAndSanitize(phoneNumber, PHONE_PATTERN, "Phone Number");
        this.phoneNumber = phoneNumber == null ? null : StringEscapeUtils.escapeHtml4(phoneNumber.trim());
    }

    public void setTelephoneAddressTypeCode(String telephoneAddressTypeCode) {
        validateAndSanitize(telephoneAddressTypeCode, NAME_PATTERN, "Telephone Address Type Code");
        this.telephoneAddressTypeCode = telephoneAddressTypeCode == null ? null : StringEscapeUtils.escapeHtml4(telephoneAddressTypeCode.trim());
    }
}

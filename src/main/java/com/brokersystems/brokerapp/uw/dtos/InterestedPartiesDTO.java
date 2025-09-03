package com.brokersystems.brokerapp.uw.dtos;

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
public class InterestedPartiesDTO {

    private static final Logger logger = LoggerFactory.getLogger(InterestedPartiesDTO.class);

    private Long ipId;
    private String ipName;
    private String ipType;
    private String ipPin;
    private String ipRegNo;
    private String polStatus;
    private String ipEmailAddress;

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

    public void setIpName(String ipName) {
        validateAndSanitize(ipName, NAME_PATTERN, "Interested Party Name");
        this.ipName = ipName == null ? null : StringEscapeUtils.escapeHtml4(ipName.trim());
    }

    public void setIpType(String ipType) {
        validateAndSanitize(ipType, NAME_PATTERN, "Interested Party Type");
        this.ipType = ipType == null ? null : StringEscapeUtils.escapeHtml4(ipType.trim());
    }

    public void setIpPin(String ipPin) {
        validateAndSanitize(ipPin, ID_NUMBER_PATTERN, "Interested Party PIN");
        this.ipPin = ipPin == null ? null : StringEscapeUtils.escapeHtml4(ipPin.trim());
    }

    public void setIpRegNo(String ipRegNo) {
        validateAndSanitize(ipRegNo, REF_PATTERN, "Interested Party Registration Number");
        this.ipRegNo = ipRegNo == null ? null : StringEscapeUtils.escapeHtml4(ipRegNo.trim());
    }

    public void setPolStatus(String polStatus) {
        validateAndSanitize(polStatus, NAME_PATTERN, "Policy Status");
        this.polStatus = polStatus == null ? null : StringEscapeUtils.escapeHtml4(polStatus.trim());
    }

    public void setIpEmailAddress(String ipEmailAddress) {
        validateAndSanitize(ipEmailAddress, EMAIL_PATTERN, "Interested Party Email Address");
        this.ipEmailAddress = ipEmailAddress == null ? null : StringEscapeUtils.escapeHtml4(ipEmailAddress.trim());
    }


}

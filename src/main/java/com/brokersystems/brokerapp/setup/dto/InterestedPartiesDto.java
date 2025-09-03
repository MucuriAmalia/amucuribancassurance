package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InterestedPartiesDto {

    private static final Logger logger = LoggerFactory.getLogger(InterestedPartiesDto.class);

    private Long partCode;
    private String partName;
    private String partType;
    private String pinNumber;
    private String postalAddress;
    private String emailAddress;
    private String telNo;
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateRegistered;
    private String regNo;

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

    public void setPartName(String partName) {
        validateAndSanitize(partName, NAME_PATTERN, "Party Name");
        this.partName = partName == null ? null : StringEscapeUtils.escapeHtml4(partName.trim());
    }

    public void setPartType(String partType) {
        validateAndSanitize(partType, REF_PATTERN, "Party Type");
        this.partType = partType == null ? null : StringEscapeUtils.escapeHtml4(partType.trim());
    }

    public void setPinNumber(String pinNumber) {
        validateAndSanitize(pinNumber, ID_NUMBER_PATTERN, "PIN Number");
        this.pinNumber = pinNumber == null ? null : StringEscapeUtils.escapeHtml4(pinNumber.trim());
    }

    public void setPostalAddress(String postalAddress) {
        validateAndSanitize(postalAddress, ADDRESS_PATTERN, "Postal Address");
        this.postalAddress = postalAddress == null ? null : StringEscapeUtils.escapeHtml4(postalAddress.trim());
    }

    public void setEmailAddress(String emailAddress) {
        validateAndSanitize(emailAddress, EMAIL_PATTERN, "Email Address");
        this.emailAddress = emailAddress == null ? null : StringEscapeUtils.escapeHtml4(emailAddress.trim());
    }

    public void setTelNo(String telNo) {
        validateAndSanitize(telNo, PHONE_PATTERN, "Telephone Number");
        this.telNo = telNo == null ? null : StringEscapeUtils.escapeHtml4(telNo.trim());
    }

    public void setRegNo(String regNo) {
        validateAndSanitize(regNo, REF_PATTERN, "Registration Number");
        this.regNo = regNo == null ? null : StringEscapeUtils.escapeHtml4(regNo.trim());
    }

}

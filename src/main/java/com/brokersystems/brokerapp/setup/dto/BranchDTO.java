package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.brokersystems.brokerapp.common.Constants.*;

public class BranchDTO {

    private static final Logger logger = LoggerFactory.getLogger(BranchDTO.class);

    private Long obId;
    private String obName;
    private String obShtDesc;
    private String address;
    private String telNumber;
    private Long userCode;
    private Long regCode;
    private String username;
    private String headoffice;

    public Long getRegCode() {
        return regCode;
    }

    public void setRegCode(Long regCode) {
        this.regCode = regCode;
    }

    public String getObShtDesc() {
        return obShtDesc;
    }

    public String getAddress() {
        return address;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public String getUsername() {
        return username;
    }

    public String getHeadoffice() {
        return headoffice;
    }

    public Long getObId() {
        return obId;
    }

    public void setObId(Long obId) {
        this.obId = obId;
    }

    public String getObName() {
        return obName;
    }


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
       // logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setObName(String obName) {
        validateAndSanitize(obName, NAME_PATTERN, "Organization Name");
        this.obName = obName == null ? null : StringEscapeUtils.escapeHtml4(obName.trim());
    }

    public void setObShtDesc(String obShtDesc) {
        validateAndSanitize(obShtDesc, NAME_PATTERN, "Organization Short Description");
        this.obShtDesc = obShtDesc == null ? null : StringEscapeUtils.escapeHtml4(obShtDesc.trim());
    }

    public void setAddress(String address) {
        validateAndSanitize(address, DESC_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setTelNumber(String telNumber) {
        validateAndSanitize(telNumber, DESC_PATTERN, "Telephone Number");
        this.telNumber = telNumber == null ? null : StringEscapeUtils.escapeHtml4(telNumber.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setHeadoffice(String headoffice) {
        validateAndSanitize(headoffice, REF_PATTERN, "Head Office");
        this.headoffice = headoffice == null ? null : StringEscapeUtils.escapeHtml4(headoffice.trim());
    }
}

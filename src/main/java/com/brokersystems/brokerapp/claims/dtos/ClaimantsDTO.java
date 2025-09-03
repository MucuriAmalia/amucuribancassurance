package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
public class ClaimantsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ClaimantsDTO.class);

    @Setter
    private Long claimantId;

    private String address;

    private String idNumber;

    private String email;

    private String mobileNo;

    private String otherNames;

    private String surname;

    private String occupation;

    @Setter
    private Long occupId;

    @Setter
    private Date createdDate;

    private String createdBy;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAddress(String address) {
        validateAndSanitize(address, DESC_PATTERN, "Address");
        this.address = address == null ? null : StringEscapeUtils.escapeHtml4(address.trim());
    }

    public void setIdNumber(String idNumber) {
        validateAndSanitize(idNumber, REF_PATTERN, "ID Number");
        this.idNumber = idNumber == null ? null : StringEscapeUtils.escapeHtml4(idNumber.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setMobileNo(String mobileNo) {
        validateAndSanitize(mobileNo, PHONE_PATTERN, "Mobile Number");
        this.mobileNo = mobileNo == null ? null : StringEscapeUtils.escapeHtml4(mobileNo.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setSurname(String surname) {
        validateAndSanitize(surname, NAME_PATTERN, "Surname");
        this.surname = surname == null ? null : StringEscapeUtils.escapeHtml4(surname.trim());
    }

    public void setOccupation(String occupation) {
        validateAndSanitize(occupation, NAME_PATTERN, "Occupation");
        this.occupation = occupation == null ? null : StringEscapeUtils.escapeHtml4(occupation.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, CREATED_BY_PATTERN, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }
}

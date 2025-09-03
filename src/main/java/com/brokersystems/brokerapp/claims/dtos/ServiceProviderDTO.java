package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Slf4j
public class ServiceProviderDTO {

    @Setter
    private Long providerId;

    @Setter
    private Long providerTypeId;

    private String name;

    private String phoneNumber;

    private String email;

    @Setter
    private Date createdDate;

    private String createdBy;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setPhoneNumber(String phoneNumber) {
        validateAndSanitize(phoneNumber, PHONE_PATTERN, "Phone Number");
        this.phoneNumber = phoneNumber == null ? null : StringEscapeUtils.escapeHtml4(phoneNumber.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, NAME_PATTERN, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }
}
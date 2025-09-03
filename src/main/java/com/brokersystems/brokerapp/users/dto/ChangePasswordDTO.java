package com.brokersystems.brokerapp.users.dto;


import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

import static com.brokersystems.brokerapp.common.Constants.CREATED_BY_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.PASSWORD_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ChangePasswordDTO implements Serializable {


    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordDTO.class);


    private String currentPassword;
    @JsonIgnore
    private String newPass;
    @JsonIgnore
    private String confirmPass;

    private String username;

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

    public void setCurrentPassword(String currentPassword) {
        validateAndSanitize(currentPassword, PASSWORD_PATTERN, "Current Password");
        this.currentPassword = currentPassword == null ? null : StringEscapeUtils.escapeHtml4(currentPassword.trim());
    }

    public void setNewPass(String newPass) {
        validateAndSanitize(newPass, PASSWORD_PATTERN, "New Password");
        this.newPass = newPass == null ? null : StringEscapeUtils.escapeHtml4(newPass.trim());
    }

    public void setConfirmPass(String confirmPass) {
        validateAndSanitize(confirmPass, PASSWORD_PATTERN, "Confirm Password");
        this.confirmPass = confirmPass == null ? null : StringEscapeUtils.escapeHtml4(confirmPass.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, CREATED_BY_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }


}

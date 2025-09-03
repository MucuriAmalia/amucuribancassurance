package com.brokersystems.brokerapp.setup.dto;

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
public class LeadmanDto {

    private static final Logger logger = LoggerFactory.getLogger(LeadmanDto.class);

    private Long id;           // user_id
    private String name;       // user_name
    private String username;   // user_username
    private String absaNo;     // user_absa_no

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

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, CREATED_BY_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setAbsaNo(String absaNo) {
        validateAndSanitize(absaNo, REF_PATTERN, "ABSA Number");
        this.absaNo = absaNo == null ? null : StringEscapeUtils.escapeHtml4(absaNo.trim());
    }
}

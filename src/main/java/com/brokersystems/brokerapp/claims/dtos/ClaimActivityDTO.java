package com.brokersystems.brokerapp.claims.dtos;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;


import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;


@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class ClaimActivityDTO {

    @Setter
    private Long activityId;

    private String activityDesc;

    private String username;

    @Setter
    private Date activityDate;

    @Setter
    private Date remDate;

    private String currentActivity;

    private String activityNotes;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setActivityDesc(String activityDesc) {
        validateAndSanitize(activityDesc, DESC_PATTERN, "Activity Description");
        this.activityDesc = activityDesc == null ? null : StringEscapeUtils.escapeHtml4(activityDesc.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setCurrentActivity(String currentActivity) {
        validateAndSanitize(currentActivity, NAME_PATTERN, "Current Activity");
        this.currentActivity = currentActivity == null ? null : StringEscapeUtils.escapeHtml4(currentActivity.trim());
    }

    public void setActivityNotes(String activityNotes) {
        validateAndSanitize(activityNotes, DESC_PATTERN, "Activity Notes");
        this.activityNotes = activityNotes == null ? null : StringEscapeUtils.escapeHtml4(activityNotes.trim());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimActivityDTO that = (ClaimActivityDTO) o;
        return activityId != null && activityId.equals(that.activityId);
    }

    @Override
    public int hashCode() {
        return activityId != null ? activityId.hashCode() : 0;
    }
}
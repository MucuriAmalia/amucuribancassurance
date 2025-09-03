package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.CREATED_BY_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

public class UserBranchesDTO implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UserBranchesDTO.class);

    private Long userBranchId;
    private Long userId;
    private Long branchId;
    private String username;
    private String branchName;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dateAssigned;
    private String userAssigned;

    public Long getUserBranchId() {
        return userBranchId;
    }

    public void setUserBranchId(Long userBranchId) {
        this.userBranchId = userBranchId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getUsername() {
        return username;
    }


    public String getBranchName() {
        return branchName;
    }


    public Date getDateAssigned() {
        return dateAssigned;
    }

    public void setDateAssigned(Date dateAssigned) {
        this.dateAssigned = dateAssigned;
    }

    public String getUserAssigned() {
        return userAssigned;
    }


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

    public void setUsername(String username) {
        validateAndSanitize(username, CREATED_BY_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setBranchName(String branchName) {
        validateAndSanitize(branchName, NAME_PATTERN, "Branch Name");
        this.branchName = branchName == null ? null : StringEscapeUtils.escapeHtml4(branchName.trim());
    }

    public void setUserAssigned(String userAssigned) {
        validateAndSanitize(userAssigned, CREATED_BY_PATTERN, "User Assigned");
        this.userAssigned = userAssigned == null ? null : StringEscapeUtils.escapeHtml4(userAssigned.trim());
    }
}

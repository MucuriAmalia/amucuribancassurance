package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Date;

@Data
public class PayeesDTO {

    @Setter
    private Long payId;

    private String fullName;

    private String telNo;

    private String email;

    private String mobileNo;

    private String createdUser;

    @Setter
    private Date createdDate;

    private String status;

    private String accountNo;

    @Setter
    private Long bankBranchId;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setFullName(String fullName) {
        validateAndSanitize(fullName, "Full Name");
        this.fullName = fullName == null ? null : StringEscapeUtils.escapeHtml4(fullName.trim());
    }

    public void setTelNo(String telNo) {
        validateAndSanitize(telNo, "Telephone Number");
        this.telNo = telNo == null ? null : StringEscapeUtils.escapeHtml4(telNo.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setMobileNo(String mobileNo) {
        validateAndSanitize(mobileNo, "Mobile Number");
        this.mobileNo = mobileNo == null ? null : StringEscapeUtils.escapeHtml4(mobileNo.trim());
    }

    public void setCreatedUser(String createdUser) {
        validateAndSanitize(createdUser, "Created User");
        this.createdUser = createdUser == null ? null : StringEscapeUtils.escapeHtml4(createdUser.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

}
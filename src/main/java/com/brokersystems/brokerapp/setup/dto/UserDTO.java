package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;
import java.sql.Timestamp;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserDTO implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(UserDTO.class);

    private String username;
    private String password;
    private String confirmPassword;
    private String status;
    private Long id;
    private String name;
    private String email;
    private Long acctId;
    private String accountType;
    private Long acctTypeId;
    private String signature;
    private String signatureContentType;
    private String resetPass;
    private String marketer;
    private MultipartFile file;
    private String accType;
    private String sendEmail;
    private String userLocked;
    private Timestamp lastLogin;
    private String absaNo;

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

    public void setPassword(String password) {
        validateAndSanitize(password, DESC_PATTERN, "Password");
        this.password = password == null ? null : StringEscapeUtils.escapeHtml4(password.trim());
    }

    public void setConfirmPassword(String confirmPassword) {
        validateAndSanitize(confirmPassword, DESC_PATTERN, "Confirm Password");
        this.confirmPassword = confirmPassword == null ? null : StringEscapeUtils.escapeHtml4(confirmPassword.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, REF_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setName(String name) {
        validateAndSanitize(name, NAME_PATTERN, "Name");
        this.name = name == null ? null : StringEscapeUtils.escapeHtml4(name.trim());
    }

    public void setEmail(String email) {
        validateAndSanitize(email, EMAIL_PATTERN, "Email");
        this.email = email == null ? null : StringEscapeUtils.escapeHtml4(email.trim());
    }

    public void setAccountType(String accountType) {
        validateAndSanitize(accountType, REF_PATTERN, "Account Type");
        this.accountType = accountType == null ? null : StringEscapeUtils.escapeHtml4(accountType.trim());
    }

    public void setSignature(String signature) {
        validateAndSanitize(signature, FILENAME_PATTERN, "Signature");
        this.signature = signature == null ? null : StringEscapeUtils.escapeHtml4(signature.trim());
    }

    public void setSignatureContentType(String signatureContentType) {
        validateAndSanitize(signatureContentType, CONTENT_TYPE_PATTERN, "Signature Content Type");
        this.signatureContentType = signatureContentType == null ? null : StringEscapeUtils.escapeHtml4(signatureContentType.trim());
    }

    public void setResetPass(String resetPass) {
        validateAndSanitize(resetPass, DESC_PATTERN, "Reset Password");
        this.resetPass = resetPass == null ? null : StringEscapeUtils.escapeHtml4(resetPass.trim());
    }

    public void setMarketer(String marketer) {
        validateAndSanitize(marketer, NAME_PATTERN, "Marketer");
        this.marketer = marketer == null ? null : StringEscapeUtils.escapeHtml4(marketer.trim());
    }

    public void setAccType(String accType) {
        validateAndSanitize(accType, REF_PATTERN, "Account Type");
        this.accType = accType == null ? null : StringEscapeUtils.escapeHtml4(accType.trim());
    }

    public void setSendEmail(String sendEmail) {
        validateAndSanitize(sendEmail, REF_PATTERN, "Send Email");
        this.sendEmail = sendEmail == null ? null : StringEscapeUtils.escapeHtml4(sendEmail.trim());
    }

    public void setUserLocked(String userLocked) {
        validateAndSanitize(userLocked, REF_PATTERN, "User Locked");
        this.userLocked = userLocked == null ? null : StringEscapeUtils.escapeHtml4(userLocked.trim());
    }

    public void setAbsaNo(String absaNo) {
        validateAndSanitize(absaNo, REF_PATTERN, "ABSA Number");
        this.absaNo = absaNo == null ? null : StringEscapeUtils.escapeHtml4(absaNo.trim());
    }


}

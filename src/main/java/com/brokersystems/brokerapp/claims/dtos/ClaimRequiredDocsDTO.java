package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Slf4j
public class ClaimRequiredDocsDTO {

    @Setter
    private Long clmRequiredId;

    private String remarks;

    @Setter
    private Date dateReceived;

    private String docRefNo;

    private String fileName;

    private String username;

    private String docName;

    private String claimStatus;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }

    public void setDocRefNo(String docRefNo) {
        validateAndSanitize(docRefNo, REF_PATTERN, "Document Reference Number");
        this.docRefNo = docRefNo == null ? null : StringEscapeUtils.escapeHtml4(docRefNo.trim());
    }

    public void setFileName(String fileName) {
        validateAndSanitize(fileName, FILENAME_PATTERN, "File Name");
        this.fileName = fileName == null ? null : StringEscapeUtils.escapeHtml4(fileName.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setDocName(String docName) {
        validateAndSanitize(docName, NAME_PATTERN, "Document Name");
        this.docName = docName == null ? null : StringEscapeUtils.escapeHtml4(docName.trim());
    }

    public void setClaimStatus(String claimStatus) {
        validateAndSanitize(claimStatus, NAME_PATTERN, "Claim Status");
        this.claimStatus = claimStatus == null ? null : StringEscapeUtils.escapeHtml4(claimStatus.trim());
    }
}
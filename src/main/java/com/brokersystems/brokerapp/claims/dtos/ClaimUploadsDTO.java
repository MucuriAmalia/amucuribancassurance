package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Slf4j
public class ClaimUploadsDTO {

    @Setter
    private Long uploadId;

    private String fileId;

    private String fileName;

    @Setter
    private Date dateUploaded;

    private String uploadedBy;

    private String uploadedComment;

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

    public void setFileId(String fileId) {
        validateAndSanitize(fileId, REF_PATTERN, "File ID");
        this.fileId = fileId == null ? null : StringEscapeUtils.escapeHtml4(fileId.trim());
    }

    public void setFileName(String fileName) {
        validateAndSanitize(fileName, FILENAME_PATTERN, "File Name");
        this.fileName = fileName == null ? null : StringEscapeUtils.escapeHtml4(fileName.trim());
    }

    public void setUploadedBy(String uploadedBy) {
        validateAndSanitize(uploadedBy, NAME_PATTERN, "Uploaded By");
        this.uploadedBy = uploadedBy == null ? null : StringEscapeUtils.escapeHtml4(uploadedBy.trim());
    }

    public void setUploadedComment(String uploadedComment) {
        validateAndSanitize(uploadedComment, DESC_PATTERN, "Uploaded Comment");
        this.uploadedComment = uploadedComment == null ? null : StringEscapeUtils.escapeHtml4(uploadedComment.trim());
    }

    public void setClaimStatus(String claimStatus) {
        validateAndSanitize(claimStatus, NAME_PATTERN, "Claim Status");
        this.claimStatus = claimStatus == null ? null : StringEscapeUtils.escapeHtml4(claimStatus.trim());
    }
}
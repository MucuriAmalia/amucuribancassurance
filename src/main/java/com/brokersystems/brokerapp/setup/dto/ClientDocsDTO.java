package com.brokersystems.brokerapp.setup.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDocsDTO {

    private static final Logger logger = LoggerFactory.getLogger(ClientDocsDTO.class);

    private Long cdId;
    private String fileId;
    private String uploadedFileName;
    private String checkSum;
    private String contentType;
    private Long reqId;
    private String reqShtDesc;
    private String reqDesc;
    private String authStatus;
    private String uploadedBy;
    private Date uploadedDate;
    private String verifiedBy;
    private Date verifiedDate;

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

    public void setFileId(String fileId) {
        validateAndSanitize(fileId, REF_PATTERN, "File ID");
        this.fileId = fileId == null ? null : StringEscapeUtils.escapeHtml4(fileId.trim());
    }

    public void setUploadedFileName(String uploadedFileName) {
        validateAndSanitize(uploadedFileName, FILENAME_PATTERN, "Uploaded File Name");
        this.uploadedFileName = uploadedFileName == null ? null : StringEscapeUtils.escapeHtml4(uploadedFileName.trim());
    }

    public void setCheckSum(String checkSum) {
        validateAndSanitize(checkSum, CHECKSUM_PATTERN, "Checksum");
        this.checkSum = checkSum == null ? null : StringEscapeUtils.escapeHtml4(checkSum.trim());
    }

    public void setContentType(String contentType) {
        validateAndSanitize(contentType, CONTENT_TYPE_PATTERN, "Content Type");
        this.contentType = contentType == null ? null : StringEscapeUtils.escapeHtml4(contentType.trim());
    }

    public void setReqShtDesc(String reqShtDesc) {
        validateAndSanitize(reqShtDesc, DESC_PATTERN, "Request Short Description");
        this.reqShtDesc = reqShtDesc == null ? null : StringEscapeUtils.escapeHtml4(reqShtDesc.trim());
    }

    public void setReqDesc(String reqDesc) {
        validateAndSanitize(reqDesc, DESC_PATTERN, "Request Description");
        this.reqDesc = reqDesc == null ? null : StringEscapeUtils.escapeHtml4(reqDesc.trim());
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, REF_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setUploadedBy(String uploadedBy) {
        validateAndSanitize(uploadedBy, CREATED_BY_PATTERN, "Uploaded By");
        this.uploadedBy = uploadedBy == null ? null : StringEscapeUtils.escapeHtml4(uploadedBy.trim());
    }

    public void setVerifiedBy(String verifiedBy) {
        validateAndSanitize(verifiedBy, CREATED_BY_PATTERN, "Verified By");
        this.verifiedBy = verifiedBy == null ? null : StringEscapeUtils.escapeHtml4(verifiedBy.trim());
    }

}

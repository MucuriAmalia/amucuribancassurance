package com.brokersystems.brokerapp.uw.dtos;

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
public class RiskDocsDTO {

    private static final Logger logger = LoggerFactory.getLogger(RiskDocsDTO.class);

    private Long docId;
    private Long riskId;
    private Long rdId;
    private Long policyId;
    private String uploadedFileName;
    private String checkSum;
    private String contentType;
    private String url;
    private String polRevNo;
    private String docShtDesc;
    private String docDesc;
    private String authStatus;
    private String uploadedBy;
    private Date uploadedDate;
    private String verifiedBy;
    private Date verifiedDate;
    private String comments;

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

    public void setUploadedFileName(String uploadedFileName) {
        validateAndSanitize(uploadedFileName, NAME_PATTERN, "Uploaded File Name");
        this.uploadedFileName = uploadedFileName == null ? null : StringEscapeUtils.escapeHtml4(uploadedFileName.trim());
    }

    public void setCheckSum(String checkSum) {
        validateAndSanitize(checkSum, REF_PATTERN, "Check Sum");
        this.checkSum = checkSum == null ? null : StringEscapeUtils.escapeHtml4(checkSum.trim());
    }

    public void setContentType(String contentType) {
        validateAndSanitize(contentType, NAME_PATTERN, "Content Type");
        this.contentType = contentType == null ? null : StringEscapeUtils.escapeHtml4(contentType.trim());
    }

    public void setUrl(String url) {
        validateAndSanitize(url, REF_PATTERN, "URL");
        this.url = url == null ? null : StringEscapeUtils.escapeHtml4(url.trim());
    }

    public void setPolRevNo(String polRevNo) {
        validateAndSanitize(polRevNo, REF_PATTERN, "Policy Revision Number");
        this.polRevNo = polRevNo == null ? null : StringEscapeUtils.escapeHtml4(polRevNo.trim());
    }

    public void setDocShtDesc(String docShtDesc) {
        validateAndSanitize(docShtDesc, DESC_PATTERN, "Document Short Description");
        this.docShtDesc = docShtDesc == null ? null : StringEscapeUtils.escapeHtml4(docShtDesc.trim());
    }

    public void setDocDesc(String docDesc) {
        validateAndSanitize(docDesc, DESC_PATTERN, "Document Description");
        this.docDesc = docDesc == null ? null : StringEscapeUtils.escapeHtml4(docDesc.trim());
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, NAME_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setUploadedBy(String uploadedBy) {
        validateAndSanitize(uploadedBy, NAME_PATTERN, "Uploaded By");
        this.uploadedBy = uploadedBy == null ? null : StringEscapeUtils.escapeHtml4(uploadedBy.trim());
    }

    public void setVerifiedBy(String verifiedBy) {
        validateAndSanitize(verifiedBy, NAME_PATTERN, "Verified By");
        this.verifiedBy = verifiedBy == null ? null : StringEscapeUtils.escapeHtml4(verifiedBy.trim());
    }

    public void setComments(String comments) {
        validateAndSanitize(comments, DESC_PATTERN, "Comments");
        this.comments = comments == null ? null : StringEscapeUtils.escapeHtml4(comments.trim());
    }


}

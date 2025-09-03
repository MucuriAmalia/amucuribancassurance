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
public class DocsDTO {

    private static final Logger logger = LoggerFactory.getLogger(DocsDTO.class);

    private String docId;
    private String docType;
    private String clientCode;
    private String clientName;
    private String originalFileName;
    private String fileExtension;
    private String userName;
    private String docData;
    private String policyNo;
    private String qoutCode;
    private String claimNo;
    private String claimantNo;

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

    public void setDocId(String docId) {
        validateAndSanitize(docId, REF_PATTERN, "Document ID");
        this.docId = docId == null ? null : StringEscapeUtils.escapeHtml4(docId.trim());
    }

    public void setDocType(String docType) {
        validateAndSanitize(docType, REF_PATTERN, "Document Type");
        this.docType = docType == null ? null : StringEscapeUtils.escapeHtml4(docType.trim());
    }

    public void setClientCode(String clientCode) {
        validateAndSanitize(clientCode, REF_PATTERN, "Client Code");
        this.clientCode = clientCode == null ? null : StringEscapeUtils.escapeHtml4(clientCode.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setOriginalFileName(String originalFileName) {
        validateAndSanitize(originalFileName, FILENAME_PATTERN, "Original File Name");
        this.originalFileName = originalFileName == null ? null : StringEscapeUtils.escapeHtml4(originalFileName.trim());
    }

    public void setFileExtension(String fileExtension) {
        validateAndSanitize(fileExtension, FILENAME_PATTERN, "File Extension");
        this.fileExtension = fileExtension == null ? null : StringEscapeUtils.escapeHtml4(fileExtension.trim());
    }

    public void setUserName(String userName) {
        validateAndSanitize(userName, CREATED_BY_PATTERN, "User Name");
        this.userName = userName == null ? null : StringEscapeUtils.escapeHtml4(userName.trim());
    }

    public void setDocData(String docData) {
        validateAndSanitize(docData, DESC_PATTERN, "Document Data");
        this.docData = docData == null ? null : StringEscapeUtils.escapeHtml4(docData.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setQoutCode(String qoutCode) {
        validateAndSanitize(qoutCode, REF_PATTERN, "Quote Code");
        this.qoutCode = qoutCode == null ? null : StringEscapeUtils.escapeHtml4(qoutCode.trim());
    }

    public void setClaimNo(String claimNo) {
        validateAndSanitize(claimNo, REF_PATTERN, "Claim Number");
        this.claimNo = claimNo == null ? null : StringEscapeUtils.escapeHtml4(claimNo.trim());
    }

    public void setClaimantNo(String claimantNo) {
        validateAndSanitize(claimantNo, REF_PATTERN, "Claimant Number");
        this.claimantNo = claimantNo == null ? null : StringEscapeUtils.escapeHtml4(claimantNo.trim());
    }

}

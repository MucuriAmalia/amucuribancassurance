package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Slf4j
public class ClaimEnquiryDTO {

    @Setter
    private Long clmId;

    private String username;

    @Setter
    private Date lossDate;

    private String riskIdentifier;

    @Setter
    private Date clmDate;

    private String clmStatus;

    private String riskId;

    @Setter
    private Date nextRevDate;

    private String policyNo;

    private String insuredName;

    private String claimNo;

    private String insuredDate;

    @Setter
    private Long balanceApprovedBy;

    @Setter
    private Date balanceApprovalDate;

    private String actualRiskId;

    private String productName;

    private String productType;

    private String branchCode;

    private String insurerName;

    private String insurerClaimRef;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setClmStatus(String clmStatus) {
        validateAndSanitize(clmStatus, NAME_PATTERN, "Claim Status");
        this.clmStatus = clmStatus == null ? null : StringEscapeUtils.escapeHtml4(clmStatus.trim());
    }

    public void setRiskId(String riskId) {
        validateAndSanitize(riskId, REF_PATTERN, "Risk ID");
        this.riskId = riskId == null ? null : StringEscapeUtils.escapeHtml4(riskId.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setInsuredName(String insuredName) {
        validateAndSanitize(insuredName, NAME_PATTERN, "Insured Name");
        this.insuredName = insuredName == null ? null : StringEscapeUtils.escapeHtml4(insuredName.trim());
    }

    public void setClaimNo(String claimNo) {
        validateAndSanitize(claimNo, REF_PATTERN, "Claim Number");
        this.claimNo = claimNo == null ? null : StringEscapeUtils.escapeHtml4(claimNo.trim());
    }

    public void setInsuredDate(String insuredDate) {
        validateAndSanitize(insuredDate, DESC_PATTERN, "Insured Date");
        this.insuredDate = insuredDate == null ? null : StringEscapeUtils.escapeHtml4(insuredDate.trim());
    }

    public void setActualRiskId(String actualRiskId) {
        validateAndSanitize(actualRiskId, REF_PATTERN, "Actual Risk ID");
        this.actualRiskId = actualRiskId == null ? null : StringEscapeUtils.escapeHtml4(actualRiskId.trim());
    }

    public void setProductName(String productName) {
        validateAndSanitize(productName, NAME_PATTERN, "Product Name");
        this.productName = productName == null ? null : StringEscapeUtils.escapeHtml4(productName.trim());
    }

    public void setProductType(String productType) {
        validateAndSanitize(productType, NAME_PATTERN, "Product Type");
        this.productType = productType == null ? null : StringEscapeUtils.escapeHtml4(productType.trim());
    }

    public void setBranchCode(String branchCode) {
        validateAndSanitize(branchCode, REF_PATTERN, "Branch Code");
        this.branchCode = branchCode == null ? null : StringEscapeUtils.escapeHtml4(branchCode.trim());
    }

    public void setInsurerName(String insurerName) {
        validateAndSanitize(insurerName, NAME_PATTERN, "Insurer Name");
        this.insurerName = insurerName == null ? null : StringEscapeUtils.escapeHtml4(insurerName.trim());
    }

    public void setInsurerClaimRef(String insurerClaimRef) {
        validateAndSanitize(insurerClaimRef, REF_PATTERN, "Insurer Claim Reference");
        this.insurerClaimRef = insurerClaimRef == null ? null : StringEscapeUtils.escapeHtml4(insurerClaimRef.trim());
    }

    public void setRiskIdentifier(String riskIdentifier) {
        validateAndSanitize(riskIdentifier, REF_PATTERN, "Risk Identifier");
        this.riskIdentifier = riskIdentifier == null ? null : StringEscapeUtils.escapeHtml4(riskIdentifier.trim());
    }
}
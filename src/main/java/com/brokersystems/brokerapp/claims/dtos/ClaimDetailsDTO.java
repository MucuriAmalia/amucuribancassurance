package com.brokersystems.brokerapp.claims.dtos;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

@Getter
@Slf4j
public class ClaimDetailsDTO {

    @Setter
    private Long taskId;

    @Setter
    private Long clmId;

    private String claimNo;

    private String insured;

    private String lossDesc;

    private String claimStatus;

    @Setter
    private Date lossDate;

    private String riskIdentifier;

    @Setter
    private Date notificationDate;

    @Setter
    private Date bookedDate;

    @Setter
    private Date nextRvwDate;

    @Setter
    private Boolean liabilityAdmission;

    private String policyNo;

    private String client;

    private String product;

    private String riskId;

    @Setter
    private BigDecimal riskValue;

    @Setter
    private Date riskWef;

    @Setter
    private Date riskWet;

    private String causation;

    @Setter
    private Long riskBindId;

    @Setter
    private Long policyBindId;

    @Setter
    private Long clmRiskId;

    @Setter
    private List<ClaimClaimantsDTO> claimants;

    @Setter
    private List<ClaimPerilDTO> perils;

    private String approvalStatus;

    private String balanceApprovedBy;

    @Setter
    private Date balanceApprovalDate;

    @Setter
    private User currentUser;

    @Setter
    private String createdBy;


    @Setter
    private BigDecimal clientBalance;

    @Setter
    private BigDecimal insBalance;

    private String refNo;

    private String insuranceName;

    @Setter
    private BigDecimal totalReserve;

    @Setter
    private BigDecimal totalPayments;

    @Setter
    private BigDecimal ostReserve;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setClaimNo(String claimNo) {
        validateAndSanitize(claimNo, REF_PATTERN, "Claim Number");
        this.claimNo = claimNo == null ? null : StringEscapeUtils.escapeHtml4(claimNo.trim());
    }

    public void setInsured(String insured) {
        validateAndSanitize(insured, NAME_PATTERN, "Insured");
        this.insured = insured == null ? null : StringEscapeUtils.escapeHtml4(insured.trim());
    }

    public void setLossDesc(String lossDesc) {
        validateAndSanitize(lossDesc, DESC_PATTERN, "Loss Description");
        this.lossDesc = lossDesc == null ? null : StringEscapeUtils.escapeHtml4(lossDesc.trim());
    }

    public void setClaimStatus(String claimStatus) {
        validateAndSanitize(claimStatus, NAME_PATTERN, "Claim Status");
        this.claimStatus = claimStatus == null ? null : StringEscapeUtils.escapeHtml4(claimStatus.trim());
    }

    public void setRiskIdentifier(String riskIdentifier) {
        validateAndSanitize(riskIdentifier, REF_PATTERN, "Risk Identifier");
        this.riskIdentifier = riskIdentifier == null ? null : StringEscapeUtils.escapeHtml4(riskIdentifier.trim());
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setClient(String client) {
        validateAndSanitize(client, NAME_PATTERN, "Client");
        this.client = client == null ? null : StringEscapeUtils.escapeHtml4(client.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }

    public void setRiskId(String riskId) {
        validateAndSanitize(riskId, REF_PATTERN, "Risk ID");
        this.riskId = riskId == null ? null : StringEscapeUtils.escapeHtml4(riskId.trim());
    }

    public void setCausation(String causation) {
        validateAndSanitize(causation, DESC_PATTERN, "Causation");
        this.causation = causation == null ? null : StringEscapeUtils.escapeHtml4(causation.trim());
    }

    public void setApprovalStatus(String approvalStatus) {
        validateAndSanitize(approvalStatus, NAME_PATTERN, "Approval Status");
        this.approvalStatus = approvalStatus == null ? null : StringEscapeUtils.escapeHtml4(approvalStatus.trim());
    }

    public void setBalanceApprovedBy(String balanceApprovedBy) {
        validateAndSanitize(balanceApprovedBy, NAME_PATTERN, "Balance Approved By");
        this.balanceApprovedBy = balanceApprovedBy == null ? null : StringEscapeUtils.escapeHtml4(balanceApprovedBy.trim());
    }

    public void setRefNo(String refNo) {
        validateAndSanitize(refNo, REF_PATTERN, "Reference Number");
        this.refNo = refNo == null ? null : StringEscapeUtils.escapeHtml4(refNo.trim());
    }

    public void setInsuranceName(String insuranceName) {
        validateAndSanitize(insuranceName, NAME_PATTERN, "Insurance Name");
        this.insuranceName = insuranceName == null ? null : StringEscapeUtils.escapeHtml4(insuranceName.trim());
    }
}
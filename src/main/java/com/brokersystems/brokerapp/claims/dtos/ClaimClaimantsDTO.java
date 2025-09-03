package com.brokersystems.brokerapp.claims.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Slf4j
public class ClaimClaimantsDTO {

    @Setter
    private Long claimantId;

    private String thirdParty;

    private String selfClaimant;

    private String tpClaimant;

    private String claimantStatus;

    @Setter
    private Date createdDate;

    private String createdBy;

    @Setter
    private BigDecimal limitAmount;

    private BigDecimal estimatedAmount;

    private String peril;

    private String perilType;


    private String coverTypeDesc;


    public String getCoverTypeDesc() { return coverTypeDesc; }
    public void setCoverTypeDesc(String coverTypeDesc) { this.coverTypeDesc = coverTypeDesc; }

    public BigDecimal getEstimatedAmount() {
        return estimatedAmount;
    }

    public void setEstimatedAmount(BigDecimal estimatedAmount) {
        this.estimatedAmount = estimatedAmount;
    }

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setThirdParty(String thirdParty) {
        validateAndSanitize(thirdParty, "Third Party");
        this.thirdParty = thirdParty == null ? null : StringEscapeUtils.escapeHtml4(thirdParty.trim());
    }

    public void setSelfClaimant(String selfClaimant) {
        validateAndSanitize(selfClaimant, "Self Claimant");
        this.selfClaimant = selfClaimant == null ? null : StringEscapeUtils.escapeHtml4(selfClaimant.trim());
    }

    public void setTpClaimant(String tpClaimant) {
        validateAndSanitize(tpClaimant, "Third Party Claimant");
        this.tpClaimant = tpClaimant == null ? null : StringEscapeUtils.escapeHtml4(tpClaimant.trim());
    }

    public void setClaimantStatus(String claimantStatus) {
        validateAndSanitize(claimantStatus, "Claimant Status");
        this.claimantStatus = claimantStatus == null ? null : StringEscapeUtils.escapeHtml4(claimantStatus.trim());
    }

    public void setCreatedBy(String createdBy) {
        validateAndSanitize(createdBy, "Created By");
        this.createdBy = createdBy == null ? null : StringEscapeUtils.escapeHtml4(createdBy.trim());
    }

    public void setPeril(String peril) {
        validateAndSanitize(peril, "Peril");
        this.peril = peril == null ? null : StringEscapeUtils.escapeHtml4(peril.trim());
    }

    public void setPerilType(String perilType) {
        validateAndSanitize(perilType, "Peril Type");
        this.perilType = perilType == null ? null : StringEscapeUtils.escapeHtml4(perilType.trim());
    }
}
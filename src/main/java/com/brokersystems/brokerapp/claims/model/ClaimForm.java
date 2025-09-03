package com.brokersystems.brokerapp.claims.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.*;

/**
 * Created by peter on 3/6/2017.
 */
@Getter
@Slf4j
public class ClaimForm {

    @Setter
    private List<PerilBean> perils;

    @Setter
    @Temporal(TemporalType.TIMESTAMP)
    private Date lossDate;

    @Setter
    private Long currencyId;

    @Setter
    private Long paymentModeId;

    @Setter
    private Long bankBranchId;

    private String accountNo;

    private String comments;

    private String invoiceNo;

    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date invoiceDate;

    @Setter
    private BigDecimal invoiceAmount;

    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date notificationDate;

    @Setter
    private Long claimantCode;

    @Setter
    private Long sprCode;

    @Setter
    private Long riskId;

    private String riskDesc;

    private String paymentType;

    @Setter
    private Long bankActCode;

    private String riskShtDesc;

    @Setter
    private Boolean selfAsClaimant;

    private String lossDesc;

    private String riskIdentifier;

    private String activityNotes;

    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date insurerDate;

    @Setter
    private Long activityId;

    private String activityDesc;

    @Setter
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date nextReviewDate;

    @Setter
    private Long nextReviewUser;

    @Setter
    private Long providerType;

    private String providerTypeDesc;

    @Setter
    private Long serviceProvider;

    private String serviceProviderName;

    private String reviewUser;

    @Setter
    private boolean liabilityAdmission;

    private String partyToBlame;

    @Setter
    private Long expireSectionId;

    private String expireSection;

    @Setter
    private boolean balanceApproved;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, REF_PATTERN, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

    public void setComments(String comments) {
        validateAndSanitize(comments, DESC_PATTERN, "Comments");
        this.comments = comments == null ? null : StringEscapeUtils.escapeHtml4(comments.trim());
    }

    public void setInvoiceNo(String invoiceNo) {
        validateAndSanitize(invoiceNo, REF_PATTERN, "Invoice Number");
        this.invoiceNo = invoiceNo == null ? null : StringEscapeUtils.escapeHtml4(invoiceNo.trim());
    }

    public void setPaymentType(String paymentType) {
        validateAndSanitize(paymentType, NAME_PATTERN, "Payment Type");
        this.paymentType = paymentType == null ? null : StringEscapeUtils.escapeHtml4(paymentType.trim());
    }

    public void setRiskDesc(String riskDesc) {
        validateAndSanitize(riskDesc, DESC_PATTERN, "Risk Description");
        this.riskDesc = riskDesc == null ? null : StringEscapeUtils.escapeHtml4(riskDesc.trim());
    }

    public void setRiskShtDesc(String riskShtDesc) {
        validateAndSanitize(riskShtDesc, DESC_PATTERN, "Risk Short Description");
        this.riskShtDesc = riskShtDesc == null ? null : StringEscapeUtils.escapeHtml4(riskShtDesc.trim());
    }

    public void setLossDesc(String lossDesc) {
        validateAndSanitize(lossDesc, DESC_PATTERN, "Loss Description");
        this.lossDesc = lossDesc == null ? null : StringEscapeUtils.escapeHtml4(lossDesc.trim());
    }

    public void setRiskIdentifier(String riskIdentifier) {
        validateAndSanitize(riskIdentifier, REF_PATTERN, "Risk Identifier");
        this.riskIdentifier = riskIdentifier == null ? null : StringEscapeUtils.escapeHtml4(riskIdentifier.trim());
    }

    public void setActivityNotes(String activityNotes) {
        validateAndSanitize(activityNotes, DESC_PATTERN, "Activity Notes");
        this.activityNotes = activityNotes == null ? null : StringEscapeUtils.escapeHtml4(activityNotes.trim());
    }

    public void setActivityDesc(String activityDesc) {
        validateAndSanitize(activityDesc, DESC_PATTERN, "Activity Description");
        this.activityDesc = activityDesc == null ? null : StringEscapeUtils.escapeHtml4(activityDesc.trim());
    }

    public void setProviderTypeDesc(String providerTypeDesc) {
        validateAndSanitize(providerTypeDesc, NAME_PATTERN, "Provider Type Description");
        this.providerTypeDesc = providerTypeDesc == null ? null : StringEscapeUtils.escapeHtml4(providerTypeDesc.trim());
    }

    public void setServiceProviderName(String serviceProviderName) {
        validateAndSanitize(serviceProviderName, NAME_PATTERN, "Service Provider Name");
        this.serviceProviderName = serviceProviderName == null ? null : StringEscapeUtils.escapeHtml4(serviceProviderName.trim());
    }

    public void setReviewUser(String reviewUser) {
        validateAndSanitize(reviewUser, NAME_PATTERN, "Review User");
        this.reviewUser = reviewUser == null ? null : StringEscapeUtils.escapeHtml4(reviewUser.trim());
    }

    public void setPartyToBlame(String partyToBlame) {
        validateAndSanitize(partyToBlame, NAME_PATTERN, "Party to Blame");
        this.partyToBlame = partyToBlame == null ? null : StringEscapeUtils.escapeHtml4(partyToBlame.trim());
    }

    public void setExpireSection(String expireSection) {
        validateAndSanitize(expireSection, NAME_PATTERN, "Expire Section");
        this.expireSection = expireSection == null ? null : StringEscapeUtils.escapeHtml4(expireSection.trim());
    }

    @Override
    public String toString() {
        return "ClaimForm{" +
                "partyToBlame='" + partyToBlame + '\'' +
                ", lossDate=" + lossDate +
                ", notificationDate=" + notificationDate +
                ", riskId=" + riskId +
                ", lossDesc='" + lossDesc + '\'' +
                ", insurerDate=" + insurerDate +
                ", activityId=" + activityId +
                ", nextReviewDate=" + nextReviewDate +
                ", nextReviewUser=" + nextReviewUser +
                ", liabilityAdmission=" + liabilityAdmission +
                ", providerType=" + providerType +
                ", serviceProvider=" + serviceProvider +
                ", expireSectionId=" + expireSectionId +
                ", expireSection=" + expireSection +
                '}';
    }
}
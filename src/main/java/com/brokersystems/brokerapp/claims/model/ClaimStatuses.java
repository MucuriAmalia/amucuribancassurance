package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.setup.model.PaymentModes;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 10/09/2019.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_statuses")
public class ClaimStatuses {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_sts_id")
    private Long clmStatusId;

    @Column(name = "clm_sts_status")
    private String currentStatus;

    @Column(name = "clm_close_reason")
    private String closeReason;

    @Column(name = "clm_sts_current")
    private String currentActivity;

    @Column(name = "clm_sts_oldstatus")
    private String oldStatus;

    @Setter
    @Column(name = "clm_sts_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCaptured;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_sts_user")
    private User capturedBy;

    @Setter
    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="clm_sts_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @Column(name = "clm_sts_remarks",length = 2000)
    private String remarks;

    @Setter
    @ManyToOne
    @JoinColumn(name = "payment_mode_id")
    private PaymentModes paymentMode;

    @Column(name = "settlement_type", length = 2000)
    private String settlementType;

    @Setter
    @Column(name = "activity_date")
    @Temporal(TemporalType.DATE)
    private Date activityDate;

    @Setter
    @Column(name = "dv_issuance_date")
    @Temporal(TemporalType.DATE)
    private Date dvIssuanceDate;

    @Setter
    @Column(name = "dv_offer_amount")
    private BigDecimal dvOfferAmount;

    @Column(name = "client_decision")
    private String clientDecision;

    @Column(name = "decline_reason", length = 1000)
    private String declineReason;

    @Setter
    @Column(name = "dv_decline_date")
    @Temporal(TemporalType.DATE)
    private Date dvDeclineDate;

    @Setter
    @Column(name = "dv_acceptance_date")
    @Temporal(TemporalType.DATE)
    private Date dvAcceptanceDate;

    @Setter
    @Column(name = "final_settlement_date")
    @Temporal(TemporalType.DATE)
    private Date finalSettlementDate;

    @Transient
    private String newStatus;

    @Setter
    @Transient
    private Long paymentId;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setSettlementType(String settlementType) {
        validateAndSanitize(settlementType, Constants.NAME_PATTERN, "Settlement Type");
        this.settlementType = settlementType == null ? null : StringEscapeUtils.escapeHtml4(settlementType.trim());
    }

    public void setCurrentStatus(String currentStatus) {
        validateAndSanitize(currentStatus, Constants.NAME_PATTERN, "Current Status");
        this.currentStatus = currentStatus == null ? null : StringEscapeUtils.escapeHtml4(currentStatus.trim());
    }

    public void setCloseReason(String closeReason) {
        validateAndSanitize(closeReason, Constants.DESC_PATTERN, "Close Reason");
        this.closeReason = closeReason == null ? null : StringEscapeUtils.escapeHtml4(closeReason.trim());
    }

    public void setCurrentActivity(String currentActivity) {
        validateAndSanitize(currentActivity, Constants.NAME_PATTERN, "Current Activity");
        this.currentActivity = currentActivity == null ? null : StringEscapeUtils.escapeHtml4(currentActivity.trim());
    }

    public void setOldStatus(String oldStatus) {
        validateAndSanitize(oldStatus, Constants.NAME_PATTERN, "Old Status");
        this.oldStatus = oldStatus == null ? null : StringEscapeUtils.escapeHtml4(oldStatus.trim());
    }

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, Constants.DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }

    public void setNewStatus(String newStatus) {
        validateAndSanitize(newStatus, Constants.NAME_PATTERN, "New Status");
        this.newStatus = newStatus == null ? null : StringEscapeUtils.escapeHtml4(newStatus.trim());
    }

    public void setClientDecision(String clientDecision) {
        validateAndSanitize(clientDecision, Constants.NAME_PATTERN, "Client Decision");
        this.clientDecision = clientDecision == null ? null : StringEscapeUtils.escapeHtml4(clientDecision.trim());
    }

    public void setDeclineReason(String declineReason) {
        validateAndSanitize(declineReason, Constants.DESC_PATTERN, "Decline Reason");
        this.declineReason = declineReason == null ? null : StringEscapeUtils.escapeHtml4(declineReason.trim());
    }

}
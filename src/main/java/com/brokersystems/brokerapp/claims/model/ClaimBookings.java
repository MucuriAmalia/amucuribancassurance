package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.setup.model.ClmCausations;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

/**
 * Created by peter on 3/4/2017.
 */

@Entity
@Table(name = "sys_brk_clm_bookings")
@Slf4j
public class ClaimBookings {

    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_id")
    private Long clmId;

    @Getter
    @Column(name = "clm_no", unique = true, nullable = false)
    private String claimNo;

    @Setter
    @Getter
    @Column(name = "clm_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date clmDate;

    @Setter
    @Getter
    @Column(name = "clm_loss_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date lossDate;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "clm_risk_id", nullable = false)
    private RiskTrans risk;

    @Getter
    @Column(name = "clm_time", nullable = false)
    private String claimTime;

    @Getter
    @Column(name = "clm_status", nullable = false)
    private String claimStatus;

    @Getter
    @Setter
    @Column(name = "clm_rejected")
    private boolean claimRejected;

    @Getter
    @Setter
    @Column(name = "clm_status_dt", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date statusDate;

    @Getter
    @Column(name = "clm_loss_desc", length = 2000)
    private String lossDesc;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "clm_booked_by", nullable = false)
    private User bookedBy;

    @Setter
    @Getter
    @Column(name = "clm_booked_dt", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date bookedDate;

    @Setter
    @Getter
    @Column(name = "clm_next_rvw_dt", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date nextReviewDate;

    @Setter
    @Getter
    @Column(name = "clm_liability_adm")
    private boolean liabilityAdmission;

    @Setter
    @Getter
    @ManyToOne
    @JoinColumn(name = "clm_status_id", nullable = false)
    private ClmCausations activity;

    @Getter
    @Column(name = "clm_party_blame")
    private String partyToBlame;

    @Setter
    @Getter
    @Column(name = "clm_insurer_dt")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date insurerDate;

    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_currency_id")
    private Currencies currency;

    @Getter
    @Column(name = "approval_status")
    private String approvalStatus;

    @Getter
    @Column(name = "risk_identifier")
    private String riskIdentifier;

    @Setter
    @ManyToOne
    @JoinColumn(name = "balance_approved_by", nullable = true)
    private User balanceApprovedBy;

    @Setter
    @ManyToOne
    @JoinColumn(name = "claim_approved_by", nullable = true)
    private User claimApprovedBy;

    @Getter
    @Setter
    @Column(name = "balance_approval_date")
    private Date balanceApprovalDate;

    @Getter
    @Setter
    @Column(name = "claim_approval_date")
    private Date claimApprovalDate;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public User getBalanceApprovedBy() {
        return claimApprovedBy;
    }

    public User getClaimApprovedBy() {
        return balanceApprovedBy;
    }

    public void setRiskIdentifier(String riskIdentifier) {
        validateAndSanitize(riskIdentifier, REF_PATTERN, "Risk Identifier");
        this.riskIdentifier = riskIdentifier == null ? null : StringEscapeUtils.escapeHtml4(riskIdentifier.trim());
    }

    public void setApprovalStatus(String approvalStatus) {
        validateAndSanitize(approvalStatus, NAME_PATTERN, "Approval Status");
        this.approvalStatus = approvalStatus == null ? null : StringEscapeUtils.escapeHtml4(approvalStatus.trim());
    }

    public void setClaimNo(String claimNo) {
        validateAndSanitize(claimNo, REF_PATTERN, "Claim Number");
        this.claimNo = claimNo == null ? null : StringEscapeUtils.escapeHtml4(claimNo.trim());
    }

    public void setClaimTime(String claimTime) {
        validateAndSanitize(claimTime, TIME_PATTERN, "Claim Time");
        this.claimTime = claimTime == null ? null : StringEscapeUtils.escapeHtml4(claimTime.trim());
    }

    public void setClaimStatus(String claimStatus) {
        validateAndSanitize(claimStatus, NAME_PATTERN, "Claim Status");
        this.claimStatus = claimStatus == null ? null : StringEscapeUtils.escapeHtml4(claimStatus.trim());
    }

    public void setLossDesc(String lossDesc) {
        validateAndSanitize(lossDesc, DESC_PATTERN, "Loss Description");
        this.lossDesc = lossDesc == null ? null : StringEscapeUtils.escapeHtml4(lossDesc.trim());
    }

    public void setPartyToBlame(String partyToBlame) {
        validateAndSanitize(partyToBlame, NAME_PATTERN, "Party to Blame");
        this.partyToBlame = partyToBlame == null ? null : StringEscapeUtils.escapeHtml4(partyToBlame.trim());
    }

}
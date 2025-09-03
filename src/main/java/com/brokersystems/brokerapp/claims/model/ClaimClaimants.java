package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 3/8/2017.
 */
@Getter
@Entity
@Table(name = "sys_brk_clm_claimants")
@Slf4j
public class ClaimClaimants {


    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_clmnt_id")
    private Long claimantId;

    @Column(name = "clm_clmnt_tp", nullable = false)
    private String thirdParty;

    @Column(name = "clm_clmnt_status")
    private String claimantStatus;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_clmnt_clm_id", nullable = false)
    private ClaimBookings claimBookings;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_clmnt_client_id")
    private ClientDef client;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_clmnt_clmnt_id")
    private ClaimantsDef claimant;

    @Setter
    @Column(name = "clm_clmnt_amount")
    private BigDecimal claimAmount;

    @Setter
    @Column(name = "clm_paid_amount")
    private BigDecimal claimPaidAmount;

    @Setter
    private Date createdDate;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_created_user")
    private User createdUser;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setThirdParty(String thirdParty) {
        validateAndSanitize(thirdParty, "Third Party");
        this.thirdParty = thirdParty == null ? null : StringEscapeUtils.escapeHtml4(thirdParty.trim());
    }

    public void setClaimantStatus(String claimantStatus) {
        validateAndSanitize(claimantStatus, "Claimant Status");
        this.claimantStatus = claimantStatus == null ? null : StringEscapeUtils.escapeHtml4(claimantStatus.trim());
    }

}
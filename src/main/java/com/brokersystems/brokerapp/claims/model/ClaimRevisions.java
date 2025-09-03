package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import com.brokersystems.brokerapp.setup.model.User;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 3/8/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_revisions")
public class ClaimRevisions {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_rev_id")
    private Long revisionId;

    @Setter
    @Column(name = "clm_rev_date")
    private Date revDate;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_rev_clm_id",nullable=false)
    private ClaimBookings claimBookings;

    @Setter
    @Column(name = "clm_rev_amt")
    private BigDecimal revAmount;

    @Column(name = "clm_rev_trans_type")
    private String transType;

    @Column(name="clm_rev_auth_status")
    private String authStatus;

    @Setter
    @Column(name="clm_rev_auth_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date authDate;

    @Setter
    @XmlTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="clm_rev_create_user")
    private User createdUser;

    @Setter
    @XmlTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="clm_rev_auth_user")
    private User authUser;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }
}
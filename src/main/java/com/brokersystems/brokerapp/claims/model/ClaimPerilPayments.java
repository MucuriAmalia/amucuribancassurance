package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

/**
 * Created by waititu on 09/09/2019.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_peril_pymnts")
public class ClaimPerilPayments {


    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_pymnt_id")
    private Long clmPymntId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_pymnt_peril_id", nullable = false)
    private ClaimPerils claimPerils;

    @Setter
    @Column(name = "clm_pymnt_amount")
    private BigDecimal clmPymntAmount;

    @Column(name = "clm_pymnt_payee", nullable = false)
    private String payee;

    @Column(name = "clm_pymnt_ref", nullable = false)
    private String pymntRef;

    @Column(name = "clm_pymnt_type", nullable = false)
    private String pymntType;

    @Setter
    @Column(name = "clm_pymnt_date", nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date pymntDate;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_pymnt_by", nullable = false)
    private User capturedBy;

    @Setter
    @Column(name = "clm_pymnt_capture_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date captureDate;

    @Column(name = "clm_pymnt_auth_status")
    private String authStatus;

    @Setter
    @Column(name = "clm_pymnt_auth_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date authDate;

    @Setter
    @XmlTransient
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_pymnt_auth_user")
    private User authUser;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAuthStatus(String authStatus) {
        validateAndSanitize(authStatus, NAME_PATTERN, "Authorization Status");
        this.authStatus = authStatus == null ? null : StringEscapeUtils.escapeHtml4(authStatus.trim());
    }

    public void setPayee(String payee) {
        validateAndSanitize(payee, NAME_PATTERN, "Payee");
        this.payee = payee == null ? null : StringEscapeUtils.escapeHtml4(payee.trim());
    }

    public void setPymntRef(String pymntRef) {
        validateAndSanitize(pymntRef, REF_PATTERN, "Payment Reference");
        this.pymntRef = pymntRef == null ? null : StringEscapeUtils.escapeHtml4(pymntRef.trim());
    }

    public void setPymntType(String pymntType) {
        validateAndSanitize(pymntType, NAME_PATTERN, "Payment Type");
        this.pymntType = pymntType == null ? null : StringEscapeUtils.escapeHtml4(pymntType.trim());
    }
}
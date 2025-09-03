package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.setup.model.BinderSectionPerils;
import com.brokersystems.brokerapp.setup.model.PerilsDef;
import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.SectionTrans;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.math.BigDecimal;

import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

/**
 * Created by peter on 3/8/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_perils")
public class ClaimPerils {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_prl_id")
    private Long clmPerilId;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_prl_clm_id", nullable = false)
    private ClaimBookings claimBookings;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_prl_peril_id", nullable = false)
    private PerilsDef perilsDef;

    @Column(name = "clm_prl_type")
    private String type;

    @Setter
    @Column(name = "clm_prl_limit_amt")
    private BigDecimal limitAmt;

    @Setter
    @Column(name = "clm_prl_excess")
    private BigDecimal excessAmt;

    @Setter
    @Column(name = "clm_prl_reserve")
    private BigDecimal reserve;

    @Column(name = "clm_prl_remark", length = 2000)
    private String remarks;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_prl_user", nullable = false)
    private User revisionBy;

    @Column(name = "clm_prl_transtype")
    private String transType;

    @Setter
    @Column(name = "clm_prl_total_reserve")
    private BigDecimal totalReserve;

    @Setter
    @Column(name = "clm_prl_orig_reserve")
    private BigDecimal Originalreserve;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_prl_clmnt_id")
    private ClaimClaimants clmClaimant;

    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "clm_prl_spr_id")
    private ClaimServiceProvider serviceProviderDef;

    @Column(name = "clm_prl_claimant")
    private String claimant;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_prl_bsp_id")
    private BinderSectionPerils binderSectionPerils;

    @Setter
    @Column(name = "clm_change_amt")
    private BigDecimal changeAmount;

    @Setter
    @Column(name = "clm_prl_paid_amount")
    private BigDecimal claimPaidAmount;

    @Setter
    @ManyToOne
    @JoinColumn(name = "clm_expiring_sect_id")
    private SectionTrans expiringSection;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + fieldName + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setType(String type) {
        validateAndSanitize(type, NAME_PATTERN, "Type");
        this.type = type == null ? null : StringEscapeUtils.escapeHtml4(type.trim());
    }

    public void setRemarks(String remarks) {
        validateAndSanitize(remarks, DESC_PATTERN, "Remarks");
        this.remarks = remarks == null ? null : StringEscapeUtils.escapeHtml4(remarks.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setClaimant(String claimant) {
        validateAndSanitize(claimant, NAME_PATTERN, "Claimant");
        this.claimant = claimant == null ? null : StringEscapeUtils.escapeHtml4(claimant.trim());
    }

}
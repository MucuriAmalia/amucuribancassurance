package com.brokersystems.brokerapp.claims.model;

import com.brokersystems.brokerapp.common.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 3/8/2017.
 */
@Getter
@Entity
@Slf4j
@Table(name = "sys_brk_clm_rev_trans")
public class ClaimRevisionTrans {

    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "clm_rv_trans_id")
    private Long revTransId;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_rv_prl_id",nullable=false)
    private ClaimPerils claimPeril;

    @Column(name = "clm_rv_type")
    private String type;

    @Setter
    @Column(name = "clm_rv_amount",nullable = false)
    private BigDecimal amount;

    @Setter
    @ManyToOne
    @JoinColumn(name="clm_rv_rev_id",nullable=false)
    private ClaimRevisions claimRevision;

    private void validateAndSanitize(String input) {
        if (input != null) {
            System.out.println("Validating and sanitizing " + "Type" + ": Raw input = '" + input + "'");
            log.debug("Validating and sanitizing {}: Raw input = '{}'", "Type", input);
        }
        if (input != null && !input.trim().isEmpty() && !input.matches(Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Type");
        }
    }

    public void setType(String type) {
        validateAndSanitize(type);
        this.type = type == null ? null : StringEscapeUtils.escapeHtml4(type.trim());
    }

}
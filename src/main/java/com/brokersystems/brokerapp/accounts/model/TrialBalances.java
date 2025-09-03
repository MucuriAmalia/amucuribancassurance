package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_trial_bals")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrialBalances {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="tb_id")
    @Setter
    private Long tbId;

    @Column(name = "tb_gl_code",length = 30)
    private String glcode;

    @Column(name = "tb_balance")
    @Setter
    private BigDecimal balance;

    @Column(name = "tb_dr")
    @Setter
    private BigDecimal debits;

    @Column(name = "tb_cr")
    @Setter
    private BigDecimal credits;

    @Column(name = "tb_yr_bal")
    @Setter
    private BigDecimal yopbalance;

    @Column(name = "tb_pre_bal")
    @Setter
    private BigDecimal prevbalance;

    @Column(name = "tb_pre_drs")
    @Setter
    private BigDecimal prevdebits;

    @Column(name = "tb_pre_crs")
    @Setter
    private BigDecimal prevcredits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tb_usr_id",nullable=false)
    @Setter
    private User user;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "GL Code");
        }
    }

    public void setGlcode(String glcode) {
        validateAndSanitize(glcode);
        this.glcode = glcode == null ? null : StringEscapeUtils.escapeHtml4(glcode.trim());
    }
}
package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_trial_trans")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TrialBalanceTrans {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="tbt_id")
    @Setter
    private Long tbtId;

    @Column(name = "tbt_gl_code",length = 30)
    private String glcode;

    @Column(name = "tbt_dr")
    @Setter
    private BigDecimal debits;

    @Column(name = "tbt_cr")
    @Setter
    private BigDecimal credits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tbt_usr_id",nullable=false)
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
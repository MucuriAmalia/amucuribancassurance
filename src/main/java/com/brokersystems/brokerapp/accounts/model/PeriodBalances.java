package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_prd_balances")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PeriodBalances {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pb_id")
    @Setter
    private Long pbId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pb_yp_id",nullable=false)
    @Setter
    private AccountYearPeriods accountYearPeriods;

    @Column(name="pb_op_balance")
    @Setter
    private BigDecimal opBalance;

    @Column(name="pb_curr_balance")
    @Setter
    private BigDecimal currBalance;

    @Column(name="pb_gl_code",length = 50)
    private String accntNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="pb_ob_id",nullable=false)
    @Setter
    private OrgBranch branch;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Account Number");
        }
    }

    public void setAccntNumber(String accntNumber) {
        validateAndSanitize(accntNumber);
        this.accntNumber = accntNumber == null ? null : StringEscapeUtils.escapeHtml4(accntNumber.trim());
    }
}
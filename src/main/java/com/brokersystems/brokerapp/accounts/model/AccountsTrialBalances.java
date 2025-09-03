package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_trial_balances")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountsTrialBalances {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="tb_id")
    @Setter
    private Long tbId;

    @Column(name = "tb_acc_no",length = 50, nullable = false)
    private String accountNo;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tb_user_id")
    @Setter
    private User generatedBy;

    @Column(name="tb_bal")
    @Setter
    private BigDecimal balance;

    @Column(name="tb_dr")
    @Setter
    private BigDecimal debits;

    @Column(name="tb_cr")
    @Setter
    private BigDecimal credits;

    @Column(name = "tb_bal_op")
    @Setter
    private boolean yearOperatingBal;

    @Column(name="tb__p_bal")
    @Setter
    private BigDecimal prevBalance;

    @Column(name="tb_p_dr")
    @Setter
    private BigDecimal prevDebits;

    @Column(name="tb_p_cr")
    @Setter
    private BigDecimal prevCredits;

    private void validateAndSanitize(String input) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + "Account Number");
        }
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo);
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }
}
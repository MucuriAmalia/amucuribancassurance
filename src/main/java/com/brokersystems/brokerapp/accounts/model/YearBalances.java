package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_yr_balances")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class YearBalances {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="yb_id")
    @Setter
    private Long ybId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="yb_yr_id",nullable=false)
    @Setter
    private AccountYears accountYears;

    @Column(name="yb_op_balance")
    @Setter
    private BigDecimal opBalance;

    @Column(name="yb_curr_balance")
    @Setter
    private BigDecimal currBalance;

    @Column(name="yb_gl_code",length = 50)
    private String accntNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="yb_ob_id",nullable=false)
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
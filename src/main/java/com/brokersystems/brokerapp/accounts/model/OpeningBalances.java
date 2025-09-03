package com.brokersystems.brokerapp.accounts.model;

import com.brokersystems.brokerapp.setup.model.OrgBranch;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_opening_bals")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpeningBalances {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="opb_Id")
    @Setter
    private Long opbId;

    @Column(name="opb_acct_no",length = 50)
    private String accountNo;

    @Column(name="opb_acct_name")
    private String accountName;

    @Column(name="opb_dr_amt")
    @Setter
    private BigDecimal debit;

    @Column(name="opb_cr_amt")
    @Setter
    private BigDecimal credit;

    @Column(name="opb_acct_yr")
    @Setter
    private Long acctYear;

    @Column(name="opb_balance")
    @Setter
    private BigDecimal balance;

    @Column(name="opb_acct_month",length = 20)
    private String acctPeriod;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rpv_ob_Id",nullable=false)
    @Setter
    private OrgBranch branch;

    @Column(name="opb_processed_type",length = 1)
    private String type;

    @Column(name="insuremaster_gl")
    @Setter
    private Long insureMasterGl;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

    public void setAccountName(String accountName) {
        validateAndSanitize(accountName, "Account Name");
        this.accountName = accountName == null ? null : StringEscapeUtils.escapeHtml4(accountName.trim());
    }

    public void setAcctPeriod(String acctPeriod) {
        validateAndSanitize(acctPeriod, "Account Period");
        this.acctPeriod = acctPeriod == null ? null : StringEscapeUtils.escapeHtml4(acctPeriod.trim());
    }

    public void setType(String type) {
        validateAndSanitize(type, "Type");
        this.type = type == null ? null : StringEscapeUtils.escapeHtml4(type.trim());
    }
}
package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;

@Entity
@Table(name="sys_brk_payee_accounts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayeeAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="payc_Id")
    @Setter
    private Long paycId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="payc_bb_id")
    @Setter
    private BankBranches bankBranches;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="payc_pay_id")
    @Setter
    private Payees payees;

    @Column(name="payc_account_no")
    private String accountNo;

    @Column(name="payc_status")
    private String status;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }
}
package com.brokersystems.brokerapp.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;

@Entity
@Table(name="sys_brk_rpt_format_accts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rfa_id")
    @Setter
    private Long rfaId;

    @Column(name="rfa_sign")
    @Setter
    private Boolean sign;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rfa_rf_id")
    @Setter
    private FinalReportFormats reportFormats;

    @Column(name="rfa_acct_no",length = 50)
    private String accountNo;

    @Column(name="rfa_acct_name",length = 300)
    private String accountName;

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
}
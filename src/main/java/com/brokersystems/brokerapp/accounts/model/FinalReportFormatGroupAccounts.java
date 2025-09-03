package com.brokersystems.brokerapp.accounts.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import javax.persistence.*;

@Entity
@Table(name="sys_brk_rpt_format_grp_accts")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatGroupAccounts {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rfga_id")
    @Setter
    private Long rfgaId;

    @Column(name="rfga_sign")
    @Setter
    private Boolean sign;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="rfga_rf_id")
    @Setter
    private FinalReportFormats reportFormats;

    @Column(name="rfa_acct_no",length = 50)
    private String accountNo;

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
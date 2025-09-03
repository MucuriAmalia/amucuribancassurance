package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FinalReportFormatAcctsDTO {

    @Setter
    private Long rfaId;

    private String accountNo;

    private String accountName;

    @Setter
    private Boolean sign;

    private String affsign;

    @Setter
    private Long rfId;

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

    public void setAffsign(String affsign) {
        validateAndSanitize(affsign, "Affiliated Sign");
        this.affsign = affsign == null ? null : StringEscapeUtils.escapeHtml4(affsign.trim());
    }

}
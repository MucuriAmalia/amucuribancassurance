package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PayeeAccountsDTO {

    @Setter
    private Long paycId;

    @Setter
    private Long bankBranchId;

    private String bankBranch;

    private String bank;

    private String accountNo;

    private String status;

    @Setter
    private Long payId;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setBankBranch(String bankBranch) {
        validateAndSanitize(bankBranch, "Bank Branch");
        this.bankBranch = bankBranch == null ? null : StringEscapeUtils.escapeHtml4(bankBranch.trim());
    }

    public void setBank(String bank) {
        validateAndSanitize(bank, "Bank");
        this.bank = bank == null ? null : StringEscapeUtils.escapeHtml4(bank.trim());
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
package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OpeningBalanceDTO {

    private String accountName;

    private String accountNo;

    private String period;

    @Setter
    private BigDecimal balance;

    @Setter
    private BigDecimal drBalance;

    @Setter
    private BigDecimal currBalance;

    @Setter
    private BigDecimal crBalance;

    private String processedType;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setAccountName(String accountName) {
        validateAndSanitize(accountName, "Account Name");
        this.accountName = accountName == null ? null : StringEscapeUtils.escapeHtml4(accountName.trim());
    }

    public void setAccountNo(String accountNo) {
        validateAndSanitize(accountNo, "Account Number");
        this.accountNo = accountNo == null ? null : StringEscapeUtils.escapeHtml4(accountNo.trim());
    }

    public void setPeriod(String period) {
        validateAndSanitize(period, "Period");
        this.period = period == null ? null : StringEscapeUtils.escapeHtml4(period.trim());
    }

    public void setProcessedType(String processedType) {
        validateAndSanitize(processedType, "Processed Type");
        this.processedType = processedType == null ? null : StringEscapeUtils.escapeHtml4(processedType.trim());
    }

}
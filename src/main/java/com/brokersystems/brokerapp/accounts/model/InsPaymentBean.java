package com.brokersystems.brokerapp.accounts.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;

@Getter
@ToString
public class InsPaymentBean {

    private String debiTrans;
    @Setter
    private BigDecimal amount;
    private String creditTrans;
    private String transType;
    @Setter
    private Long acctCode;
    @Setter
    private Long accountCode;
    @Setter
    private Long transId;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setDebiTrans(String debiTrans) {
        validateAndSanitize(debiTrans, "Debit Transaction");
        this.debiTrans = debiTrans == null ? null : StringEscapeUtils.escapeHtml4(debiTrans.trim());
    }

    public void setCreditTrans(String creditTrans) {
        validateAndSanitize(creditTrans, "Credit Transaction");
        this.creditTrans = creditTrans == null ? null : StringEscapeUtils.escapeHtml4(creditTrans.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }
}
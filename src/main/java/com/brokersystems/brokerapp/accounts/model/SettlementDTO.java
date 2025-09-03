package com.brokersystems.brokerapp.accounts.model;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;

/**
 * Created by HP on 5/13/2018.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SettlementDTO {

    private String polNo;
    private String clientPolNo;
    private String clientName;
    private String drNo;
    private String crNo;
    private String payStatus;
    @Setter
    private BigDecimal basicPrem;
    @Setter
    private BigDecimal commAmt;
    @Setter
    private BigDecimal whtx;
    @Setter
    private BigDecimal debitBal;
    @Setter
    private BigDecimal allocAmt;
    @Setter
    private BigDecimal settleAmt;
    private String transType;
    @Setter
    private Long acctCode;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setPolNo(String polNo) {
        validateAndSanitize(polNo, "Policy Number");
        this.polNo = polNo == null ? null : StringEscapeUtils.escapeHtml4(polNo.trim());
    }

    public void setClientPolNo(String clientPolNo) {
        validateAndSanitize(clientPolNo, "Client Policy Number");
        this.clientPolNo = clientPolNo == null ? null : StringEscapeUtils.escapeHtml4(clientPolNo.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setDrNo(String drNo) {
        validateAndSanitize(drNo, "Debit Number");
        this.drNo = drNo == null ? null : StringEscapeUtils.escapeHtml4(drNo.trim());
    }

    public void setCrNo(String crNo) {
        validateAndSanitize(crNo, "Credit Number");
        this.crNo = crNo == null ? null : StringEscapeUtils.escapeHtml4(crNo.trim());
    }

    public void setPayStatus(String payStatus) {
        validateAndSanitize(payStatus, "Payment Status");
        this.payStatus = payStatus == null ? null : StringEscapeUtils.escapeHtml4(payStatus.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

}
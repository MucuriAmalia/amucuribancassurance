package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SettlementDTO {

    private String polNo;

    private Long transId;

    private String clientPolNo;

    private String clientName;

    private String product;

    private String insurer;

    private String drNo;

    private String crNo;

    private String payStatus;

    @Setter
    private BigDecimal basicPrem;

    @Setter
    private BigDecimal commNetAmt;

    @Setter
    private BigDecimal commAmt;

    @Setter
    private BigDecimal drCommAmt;

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

    @Setter
    private Long agentCode;

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

    public void setProduct(String product) {
        validateAndSanitize(product, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }

    public void setInsurer(String insurer) {
        validateAndSanitize(insurer, "Insurer");
        this.insurer = insurer == null ? null : StringEscapeUtils.escapeHtml4(insurer.trim());
    }

    public void setDrNo(String drNo) {
        validateAndSanitize(drNo, "Debit Note");
        this.drNo = drNo == null ? null : StringEscapeUtils.escapeHtml4(drNo.trim());
    }

    public void setCrNo(String crNo) {
        validateAndSanitize(crNo, "Credit Note");
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


    public void setTransId(Long transId) {
        this.transId = transId;
    }
}
package com.brokersystems.brokerapp.trans.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.brokersystems.brokerapp.trans.model.ReceiptTransDtls;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateReceiptDTO {

    private static final Logger logger = LoggerFactory.getLogger(CreateReceiptDTO.class);

    private Long receiptId;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date receiptDate;

    private Date receiptTransDate;

    private BigDecimal receiptAmount;

    private Long insuranceId;

    private Long brnCode;

    private String receiptType;

    private Long payId;

    private String paymentRef;

    private String paidBy;

    private String fromFCR;

    private String directReceipt;

    private Long policyId;

    @Transient
    private List<ReceiptTransDtls> details;


    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setReceiptType(String receiptType) {
        validateAndSanitize(receiptType, REF_PATTERN, "Receipt Type");
        this.receiptType = receiptType == null ? null : StringEscapeUtils.escapeHtml4(receiptType.trim());
    }

    public void setPaymentRef(String paymentRef) {
        validateAndSanitize(paymentRef, REF_PATTERN, "Payment Reference");
        this.paymentRef = paymentRef == null ? null : StringEscapeUtils.escapeHtml4(paymentRef.trim());
    }

    public void setPaidBy(String paidBy) {
        validateAndSanitize(paidBy, NAME_PATTERN, "Paid By");
        this.paidBy = paidBy == null ? null : StringEscapeUtils.escapeHtml4(paidBy.trim());
    }

    public void setFromFCR(String fromFCR) {
        validateAndSanitize(fromFCR, REF_PATTERN, "From FCR");
        this.fromFCR = fromFCR == null ? null : StringEscapeUtils.escapeHtml4(fromFCR.trim());
    }

    public void setDirectReceipt(String directReceipt) {
        validateAndSanitize(directReceipt, REF_PATTERN, "Direct Receipt");
        this.directReceipt = directReceipt == null ? null : StringEscapeUtils.escapeHtml4(directReceipt.trim());
    }
}

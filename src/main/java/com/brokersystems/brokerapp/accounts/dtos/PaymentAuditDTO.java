package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;
import static com.brokersystems.brokerapp.common.Constants.DESC_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentAuditDTO {

    private String refNo;

    private String clientPolNo;

    private String fname;

    private String otherNames;

    private String controlAcc;

    private String proDesc;

    @Setter
    private BigDecimal paymentAmount;

    @Setter
    private BigDecimal commAmount;

    @Setter
    private BigDecimal whtxAmount;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(pattern)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    public void setRefNo(String refNo) {
        validateAndSanitize(refNo, NAME_PATTERN, "Reference Number");
        this.refNo = refNo == null ? null : StringEscapeUtils.escapeHtml4(refNo.trim());
    }

    public void setClientPolNo(String clientPolNo) {
        validateAndSanitize(clientPolNo, NAME_PATTERN, "Client Policy Number");
        this.clientPolNo = clientPolNo == null ? null : StringEscapeUtils.escapeHtml4(clientPolNo.trim());
    }

    public void setFname(String fname) {
        validateAndSanitize(fname, NAME_PATTERN, "First Name");
        this.fname = fname == null ? null : StringEscapeUtils.escapeHtml4(fname.trim());
    }

    public void setOtherNames(String otherNames) {
        validateAndSanitize(otherNames, NAME_PATTERN, "Other Names");
        this.otherNames = otherNames == null ? null : StringEscapeUtils.escapeHtml4(otherNames.trim());
    }

    public void setControlAcc(String controlAcc) {
        validateAndSanitize(controlAcc, NAME_PATTERN, "Control Account");
        this.controlAcc = controlAcc == null ? null : StringEscapeUtils.escapeHtml4(controlAcc.trim());
    }

    public void setProDesc(String proDesc) {
        validateAndSanitize(proDesc, DESC_PATTERN, "Product Description");
        this.proDesc = proDesc == null ? null : StringEscapeUtils.escapeHtml4(proDesc.trim());
    }

}
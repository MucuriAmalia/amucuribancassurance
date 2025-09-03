package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OverpaidPolicyDTO {

    private static final Logger logger = LoggerFactory.getLogger(OverpaidPolicyDTO.class);

    private String policyNo;
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date maturityDate;
    private String client;
    private BigDecimal totalPremium;
    private BigDecimal paidPremium;
    private BigDecimal refundPremium;
    private String policyType;

    private void validateAndSanitize(String input, String pattern, String fieldName) {
        if (input == null) {
            return;
        }
        //logger.debug("Validating and sanitizing {}: Raw input = '{}'", fieldName, input);
        String trimmedInput = input.trim();
        if (!trimmedInput.isEmpty() && !trimmedInput.matches(pattern)) {
            throw new IllegalArgumentException(
                    String.format("Invalid characters in %s: '%s' does not match pattern %s", fieldName, trimmedInput, pattern)
            );
        }
    }

    public void setPolicyNo(String policyNo) {
        validateAndSanitize(policyNo, REF_PATTERN, "Policy Number");
        this.policyNo = policyNo == null ? null : StringEscapeUtils.escapeHtml4(policyNo.trim());
    }

    public void setClient(String client) {
        validateAndSanitize(client, NAME_PATTERN, "Client");
        this.client = client == null ? null : StringEscapeUtils.escapeHtml4(client.trim());
    }

    public void setPolicyType(String policyType) {
        validateAndSanitize(policyType, NAME_PATTERN, "Policy Type");
        this.policyType = policyType == null ? null : StringEscapeUtils.escapeHtml4(policyType.trim());
    }
}

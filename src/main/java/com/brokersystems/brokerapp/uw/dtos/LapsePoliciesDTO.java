package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LapsePoliciesDTO {

    private static final Logger logger = LoggerFactory.getLogger(LapsePoliciesDTO.class);

    private Long policyId;
    private String policyNo;
    private String clientPolNo;
    private String polRevNo;
    private String clientName;
    private String agentName;
    private String binderName;
    private Long polUwYr;
    private Boolean renewable;
    private String product;
    private Date wefDate;
    private Date wetDate;
    private String currency;
    private String username;
    private Long count;
    private String idNo;
    private String currentStatus;
    private String authComments;
    public String getAuthComments() {
        return authComments;
    }



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

    public void setClientPolNo(String clientPolNo) {
        validateAndSanitize(clientPolNo, REF_PATTERN, "Client Policy Number");
        this.clientPolNo = clientPolNo == null ? null : StringEscapeUtils.escapeHtml4(clientPolNo.trim());
    }

    public void setPolRevNo(String polRevNo) {
        validateAndSanitize(polRevNo, REF_PATTERN, "Policy Revision Number");
        this.polRevNo = polRevNo == null ? null : StringEscapeUtils.escapeHtml4(polRevNo.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setAgentName(String agentName) {
        validateAndSanitize(agentName, NAME_PATTERN, "Agent Name");
        this.agentName = agentName == null ? null : StringEscapeUtils.escapeHtml4(agentName.trim());
    }

    public void setBinderName(String binderName) {
        validateAndSanitize(binderName, NAME_PATTERN, "Binder Name");
        this.binderName = binderName == null ? null : StringEscapeUtils.escapeHtml4(binderName.trim());
    }

    public void setProduct(String product) {
        validateAndSanitize(product, NAME_PATTERN, "Product");
        this.product = product == null ? null : StringEscapeUtils.escapeHtml4(product.trim());
    }

    public void setCurrency(String currency) {
        validateAndSanitize(currency, NAME_PATTERN, "Currency");
        this.currency = currency == null ? null : StringEscapeUtils.escapeHtml4(currency.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setIdNo(String idNo) {
        validateAndSanitize(idNo, ID_NUMBER_PATTERN, "ID Number");
        this.idNo = idNo == null ? null : StringEscapeUtils.escapeHtml4(idNo.trim());
    }

    public void setCurrentStatus(String currentStatus) {
        validateAndSanitize(currentStatus, NAME_PATTERN, "Current Status");
        this.currentStatus = currentStatus == null ? null : StringEscapeUtils.escapeHtml4(currentStatus.trim());
    }

    public void setAuthComments(String authComments) {
        validateAndSanitize(authComments, DESC_PATTERN, "Authorization Comments");
        this.authComments = authComments == null ? null : StringEscapeUtils.escapeHtml4(authComments.trim());
    }

}

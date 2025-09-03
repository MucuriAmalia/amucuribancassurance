package com.brokersystems.brokerapp.uw.dtos;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PortfolioDTO {

    private static final Logger logger = LoggerFactory.getLogger(PortfolioDTO.class);

    private String polNo;
    private String clientPolNo;
    private String currentStatus;
    private String polRevNo;
    private Long polId;
    private String clientName;
    private String agentName;
    private Long polUwYr;
    private String binderName;
    private Boolean renewable;
    private String product;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date wefDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date wetDate;
    private String currency;
    private String username;
    private String idNo;
    private String status;
    private String authComments;

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

    public void setPolNo(String polNo) {
        validateAndSanitize(polNo, REF_PATTERN, "Policy Number");
        this.polNo = polNo == null ? null : StringEscapeUtils.escapeHtml4(polNo.trim());
    }

    public void setClientPolNo(String clientPolNo) {
        validateAndSanitize(clientPolNo, REF_PATTERN, "Client Policy Number");
        this.clientPolNo = clientPolNo == null ? null : StringEscapeUtils.escapeHtml4(clientPolNo.trim());
    }

    public void setCurrentStatus(String currentStatus) {
        validateAndSanitize(currentStatus, NAME_PATTERN, "Current Status");
        this.currentStatus = currentStatus == null ? null : StringEscapeUtils.escapeHtml4(currentStatus.trim());
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

    public void setStatus(String status) {
        validateAndSanitize(status, NAME_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }

    public void setAuthComments(String authComments) {
        validateAndSanitize(authComments, DESC_PATTERN, "Authorization Comments");
        this.authComments = authComments == null ? null : StringEscapeUtils.escapeHtml4(authComments.trim());
    }
}

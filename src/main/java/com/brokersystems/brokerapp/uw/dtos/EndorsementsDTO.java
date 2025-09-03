package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Data;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

@Data
public class EndorsementsDTO {

    private static final Logger logger = LoggerFactory.getLogger(EndorsementsDTO.class);

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
    private String authComments;
    private String transType;

    private EndorsementsDTO(final Long policyId, final String policyNo, final String clientPolNo, final String polRevNo,
                            final String clientName, final String agentName, final String binderName, final Long polUwYr,
                            final Boolean renewable , final String product, final Date wefDate, final Date wetDate,
                            final String currency, final String username, final Long count, final String idNo, final String authComments,final String transType) {

        this.policyId = policyId;
        this.setPolicyNo(policyNo);
        this.setClientPolNo(clientPolNo);
        this.setPolRevNo(polRevNo);
        this.setClientName(clientName);
        this.setAgentName(agentName);
        this.setBinderName(binderName);
        this.polUwYr = polUwYr;
        this.renewable = renewable;
        this.setProduct(product);
        this.wefDate = wefDate;
        this.wetDate = wetDate;
        this.setCurrency(currency);
        this.setUsername(username);
        this.count = count;
        this.setIdNo(idNo);
        this.setAuthComments(authComments);
        this.setTransType(transType);

    }

    public static EndorsementsDTO instance(final Long policyId, final String policyNo, final String clientPolNo, final String polRevNo,
                                           final String clientName, final String agentName, final String binderName, final Long polUwYr,
                                           final Boolean renewable , final String product, final Date wefDate, final Date wetDate,
                                           final String currency, final String username, final Long count, final String idNo,final String authComments,final String transType)  {
        return new EndorsementsDTO(policyId,policyNo,clientPolNo,polRevNo,clientName,agentName,binderName,polUwYr,renewable,product,wefDate,wetDate,currency,username,
                count, idNo, authComments,transType);
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

    public void setAuthComments(String authComments) {
        validateAndSanitize(authComments, DESC_PATTERN, "Authorization Comments");
        this.authComments = authComments == null ? null : StringEscapeUtils.escapeHtml4(authComments.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }


}

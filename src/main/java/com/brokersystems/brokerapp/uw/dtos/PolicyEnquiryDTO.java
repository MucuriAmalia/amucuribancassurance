package com.brokersystems.brokerapp.uw.dtos;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.NAME_PATTERN;
import static com.brokersystems.brokerapp.common.Constants.REF_PATTERN;

public class PolicyEnquiryDTO {

    private static final Logger logger = LoggerFactory.getLogger(PolicyEnquiryDTO.class);

    private String polNo;
    private String proposalNo;
    private Date polCreateddt;
    private Date wef;
    private Date wet;
    private String user;
    private String currentStatus;
    private Date authDate;
    private BigDecimal premium;
    private Long transactionId;

    public String getProposalNo() {
        return proposalNo;
    }

    public Date getPolCreateddt() {
        return polCreateddt;
    }

    public void setPolCreateddt(Date polCreateddt) {
        this.polCreateddt = polCreateddt;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public String getPolNo() {
        return polNo;
    }

    public Date getWef() {
        return wef;
    }

    public void setWef(Date wef) {
        this.wef = wef;
    }

    public Date getWet() {
        return wet;
    }

    public void setWet(Date wet) {
        this.wet = wet;
    }

    public String getUser() {
        return user;
    }

    public Date getAuthDate() {
        return authDate;
    }

    public void setAuthDate(Date authDate) {
        this.authDate = authDate;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
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

    public void setPolNo(String polNo) {
        validateAndSanitize(polNo, REF_PATTERN, "Policy Number");
        this.polNo = polNo == null ? null : StringEscapeUtils.escapeHtml4(polNo.trim());
    }

    public void setProposalNo(String proposalNo) {
        validateAndSanitize(proposalNo, REF_PATTERN, "Proposal Number");
        this.proposalNo = proposalNo == null ? null : StringEscapeUtils.escapeHtml4(proposalNo.trim());
    }

    public void setUser(String user) {
        validateAndSanitize(user, NAME_PATTERN, "User");
        this.user = user == null ? null : StringEscapeUtils.escapeHtml4(user.trim());
    }

    public void setCurrentStatus(String currentStatus) {
        validateAndSanitize(currentStatus, NAME_PATTERN, "Current Status");
        this.currentStatus = currentStatus == null ? null : StringEscapeUtils.escapeHtml4(currentStatus.trim());
    }
}

package com.brokersystems.brokerapp.accounts.dtos;

import lombok.*;
import org.apache.commons.lang3.StringEscapeUtils;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class SystemTransDTO {

    private final Long transno;

    private final Long transTempNo;

    private final Date transDate;

    private final String origin;

    private final String client;

    private final String agent;

    private final String controlAcc;

    private final String refNo;

    private final String branch;

    private final String transType;

    private final String transdc;

    private final BigDecimal amount;

    private final BigDecimal netAmount;

    private final BigDecimal balance;

    private final String polNo;

    private final String payeeName;

    private void validateAndSanitize(String input, String fieldName) {
        if (input != null && !input.trim().isEmpty() && !input.matches(com.brokersystems.brokerapp.common.Constants.NAME_PATTERN)) {
            throw new IllegalArgumentException("Invalid characters in " + fieldName);
        }
    }

    private SystemTransDTO(final Long transno, final Long transTempNo, final Date transDate, final String origin, final String client, final String agent, final String controlAcc, final String refNo, final String branch, final String transType, final String transdc, final BigDecimal amount, final BigDecimal netAmount, final BigDecimal balance, final String polNo, final String payeeName) {
        validateAndSanitize(origin, "Origin");
        validateAndSanitize(client, "Client");
        validateAndSanitize(agent, "Agent");
        validateAndSanitize(controlAcc, "Control Account");
        validateAndSanitize(refNo, "Reference Number");
        validateAndSanitize(branch, "Branch");
        validateAndSanitize(transType, "Transaction Type");
        validateAndSanitize(transdc, "Transaction DC");
        validateAndSanitize(polNo, "Policy Number");
        validateAndSanitize(payeeName, "Payee Name");
        this.transno = transno;
        this.transTempNo = transTempNo;
        this.transDate = transDate;
        this.origin = origin == null ? null : StringEscapeUtils.escapeHtml4(origin.trim());
        this.client = client == null ? null : StringEscapeUtils.escapeHtml4(client.trim());
        this.agent = agent == null ? null : StringEscapeUtils.escapeHtml4(agent.trim());
        this.controlAcc = controlAcc == null ? null : StringEscapeUtils.escapeHtml4(controlAcc.trim());
        this.refNo = refNo == null ? null : StringEscapeUtils.escapeHtml4(refNo.trim());
        this.branch = branch == null ? null : StringEscapeUtils.escapeHtml4(branch.trim());
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
        this.transdc = transdc == null ? null : StringEscapeUtils.escapeHtml4(transdc.trim());
        this.amount = amount;
        this.netAmount = netAmount;
        this.balance = balance;
        this.polNo = polNo == null ? null : StringEscapeUtils.escapeHtml4(polNo.trim());
        this.payeeName = payeeName == null ? null : StringEscapeUtils.escapeHtml4(payeeName.trim());
    }

    public static SystemTransDTO instance(final Long transno, final Long transTempNo, final Date transDate, final String origin, final String client, final String agent, final String controlAcc, final String refNo, final String branch, final String transType, final String transdc, final BigDecimal amount, final BigDecimal netAmount, final BigDecimal balance, final String polNo, final String payeeName) {
        return new SystemTransDTO(transno, transTempNo, transDate, origin, client, agent, controlAcc, refNo, branch, transType, transdc, amount, netAmount, balance, polNo, payeeName);
    }

}
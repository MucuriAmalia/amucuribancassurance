package com.brokersystems.brokerapp.workflow.dto;

import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static com.brokersystems.brokerapp.common.Constants.*;

public class WorkFlowDTO {

    private static final Logger logger = LoggerFactory.getLogger(WorkFlowDTO.class);

    private Long taskId;
    private String activeProcess;
    private String refNo;
    private String clientName;
    private String username;
    private Date createdDate;
    private Long transactionId;
    private String authComments;
    private String transType;
    private String status;

    public String getAuthComments() {
        return authComments;
    }


    public String getStatus() {
        return status;
    }


    public String getTransType() {
        return transType;
    }


    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getActiveProcess() {
        return activeProcess;
    }


    public String getRefNo() {
        return refNo;
    }


    public String getClientName() {
        return clientName;
    }


    public String getUsername() {
        return username;
    }


    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
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

    public void setActiveProcess(String activeProcess) {
        validateAndSanitize(activeProcess, NAME_PATTERN, "Active Process");
        this.activeProcess = activeProcess == null ? null : StringEscapeUtils.escapeHtml4(activeProcess.trim());
    }

    public void setRefNo(String refNo) {
        validateAndSanitize(refNo, REF_PATTERN, "Reference Number");
        this.refNo = refNo == null ? null : StringEscapeUtils.escapeHtml4(refNo.trim());
    }

    public void setClientName(String clientName) {
        validateAndSanitize(clientName, NAME_PATTERN, "Client Name");
        this.clientName = clientName == null ? null : StringEscapeUtils.escapeHtml4(clientName.trim());
    }

    public void setUsername(String username) {
        validateAndSanitize(username, NAME_PATTERN, "Username");
        this.username = username == null ? null : StringEscapeUtils.escapeHtml4(username.trim());
    }

    public void setAuthComments(String authComments) {
        validateAndSanitize(authComments, DESC_PATTERN, "Authorization Comments");
        this.authComments = authComments == null ? null : StringEscapeUtils.escapeHtml4(authComments.trim());
    }

    public void setTransType(String transType) {
        validateAndSanitize(transType, NAME_PATTERN, "Transaction Type");
        this.transType = transType == null ? null : StringEscapeUtils.escapeHtml4(transType.trim());
    }

    public void setStatus(String status) {
        validateAndSanitize(status, NAME_PATTERN, "Status");
        this.status = status == null ? null : StringEscapeUtils.escapeHtml4(status.trim());
    }


}

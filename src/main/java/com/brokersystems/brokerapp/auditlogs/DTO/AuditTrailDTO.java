package com.brokersystems.brokerapp.auditlogs.DTO;

import java.util.Date;

public class AuditTrailDTO {
    private Long id;
    private String fieldName;
    private String oldValue;
    private String newValue;
    private Date auditTime;
    private String resubmissionComment;
    private String userRejectedReason;
    private Long transactionId;
    private Long taskCode;
    private Long policyId;
    private Long rtsPolicyId;
    private String policyInfo;
    private String checkerName;
    private String makerName;
    private String rejectionReasonDesc;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(String oldValue) {
        this.oldValue = oldValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }

    public String getResubmissionComment() {
        return resubmissionComment;
    }

    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment;
    }

    public String getUserRejectedReason() {
        return userRejectedReason;
    }

    public void setUserRejectedReason(String userRejectedReason) {
        this.userRejectedReason = userRejectedReason;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getTaskCode() {
        return taskCode;
    }

    public void setTaskCode(Long taskCode) {
        this.taskCode = taskCode;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public Long getRtsPolicyId() {
        return rtsPolicyId;
    }

    public void setRtsPolicyId(Long rtsPolicyId) {
        this.rtsPolicyId = rtsPolicyId;
    }

    public String getPolicyInfo() {
        return policyInfo;
    }

    public void setPolicyInfo(String policyInfo) {
        this.policyInfo = policyInfo;
    }

    public String getCheckerName() {
        return checkerName;
    }

    public void setCheckerName(String checkerName) {
        this.checkerName = checkerName;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String makerName) {
        this.makerName = makerName;
    }

    public String getRejectionReasonDesc() {
        return rejectionReasonDesc;
    }

    public void setRejectionReasonDesc(String rejectionReasonDesc) {
        this.rejectionReasonDesc = rejectionReasonDesc;
    }
}
package com.brokersystems.brokerapp.claims.dtos;

import java.util.Date;

public class ClaimAuditLogDTO {
    private Long claimId;
    private String claimNo;
    private String eventType;
    private Date eventDate;
    private Long eventUserId;
    private String eventDescription;
    private String amount;
    private String additionalInfo;
    private Date makerMadeOn;
    private Date checkerCheckedOn;
    private String action;
    private String rejectedReason;
    private String username;
    private String resubmissionComment;


    public Long getClaimId() {
        return claimId;
    }

    public void setClaimId(Long claimId) {
        this.claimId = claimId;
    }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public Long getEventUserId() {
        return eventUserId;
    }

    public void setEventUserId(Long eventUserId) {
        this.eventUserId = eventUserId;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public Date getMakerMadeOn() {
        return makerMadeOn;
    }

    public void setMakerMadeOn(Date makerMadeOn) {
        this.makerMadeOn = makerMadeOn;
    }

    public Date getCheckerCheckedOn() {
        return checkerCheckedOn;
    }

    public void setCheckerCheckedOn(Date checkerCheckedOn) {
        this.checkerCheckedOn = checkerCheckedOn;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getRejectedReason() {
        return rejectedReason;
    }

    public void setRejectedReason(String rejectedReason) {
        this.rejectedReason = rejectedReason;
    }
    public String getResubmissionComment() {
        return resubmissionComment;
    }
    public void setResubmissionComment(String resubmissionComment) {
        this.resubmissionComment = resubmissionComment;
    }


}

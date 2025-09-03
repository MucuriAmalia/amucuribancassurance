package com.brokersystems.brokerapp.bulktransactions.models;

public class BatchRecordDTO {

    private Long batchId;
    private Long userId;
    private  String rejectReason;

    public String getRejectReason() {return rejectReason;}
    public void setRejectReason(String rejectReason) {this.rejectReason = rejectReason;}
    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

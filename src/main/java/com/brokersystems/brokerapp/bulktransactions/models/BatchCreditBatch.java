package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.uw.model.RenewalRecord;

import java.util.List;

public class BatchCreditBatch {

    private Long userId;

    private List<BatchRecord> batchRecords;

    public List<BatchRecord> getBatchRecords() {
        return batchRecords;
    }

    public void setBatchRecords(List<BatchRecord> batchRecords) {
        this.batchRecords = batchRecords;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

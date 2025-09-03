package com.brokersystems.brokerapp.trans.model;

import lombok.Data;

import java.util.List;

@Data
public class CommissionData {

    List<Long> commissionids;

    Long transId;

    public List<Long> getCommissionids() {
        return commissionids;
    }

    public void setCommissionids(List<Long> commissionids) {
        this.commissionids = commissionids;
    }

    public Long getTransId() {
        return transId;
    }

    public void setTransId(Long transId) {
        this.transId = transId;
    }
}

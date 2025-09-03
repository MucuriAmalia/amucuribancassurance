package com.brokersystems.brokerapp.uw.model;

import java.util.List;

/**
 * Created by peter on 6/9/2017.
 */
public class AdminFeePolicyBean {

    private Long adminFeeId;

    private List<Long> policies;

    public Long getAdminFeeId() {
        return adminFeeId;
    }

    public void setAdminFeeId(Long adminFeeId) {
        this.adminFeeId = adminFeeId;
    }

    public List<Long> getPolicies() {
        return policies;
    }

    public void setPolicies(List<Long> policies) {
        this.policies = policies;
    }
}

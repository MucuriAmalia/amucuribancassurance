package com.brokersystems.brokerapp.uw.model;

import java.util.List;

/**
 * Created by HP on 9/13/2017.
 */
public class RiskIntPartiesBean {

    private Long riskId;
    private List<Long> parties;

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public List<Long> getParties() {
        return parties;
    }

    public void setParties(List<Long> parties) {
        this.parties = parties;
    }
}

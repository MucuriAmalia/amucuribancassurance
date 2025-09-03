package com.brokersystems.brokerapp.uw.model;

import java.util.List;

/**
 * Created by peter on 2/8/2017.
 */
public class PolicyTaxBean {

    private Long polId;

    private List<Long> taxes;

    public Long getPolId() {
        return polId;
    }

    public void setPolId(Long polId) {
        this.polId = polId;
    }

    public List<Long> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Long> taxes) {
        this.taxes = taxes;
    }
}

package com.brokersystems.brokerapp.setup.model;

import java.util.List;

/**
 * Created by HP on 3/17/2018.
 */
public class ClausesBean {

    private List<Long> clauses;
    private Long premRatesId;

    public List<Long> getClauses() {
        return clauses;
    }

    public void setClauses(List<Long> clauses) {
        this.clauses = clauses;
    }

    public Long getPremRatesId() {
        return premRatesId;
    }

    public void setPremRatesId(Long premRatesId) {
        this.premRatesId = premRatesId;
    }
}

package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by peter on 5/4/2017.
 */
public class AilmentsBean {

    private Long bindCode;

    private List<Long> ailments;

    private List<Long> claimNetworks;

    private List<Long> clauseExclusions;

    public List<Long> getClauseExclusions() {
        return clauseExclusions;
    }

    public void setClauseExclusions(List<Long> clauseExclusions) {
        this.clauseExclusions = clauseExclusions;
    }



    public List<Long> getClaimNetworks() {
        return claimNetworks;
    }

    public void setClaimNetworks(List<Long> claimNetworks) {
        this.claimNetworks = claimNetworks;
    }

    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public List<Long> getAilments() {
        return ailments;
    }

    public void setAilments(List<Long> ailments) {
        this.ailments = ailments;
    }
}

package com.brokersystems.brokerapp.quotes.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Created by peter on 3/12/2017.
 */
public class QuoteProductBean {

    @Transient
    private Long prodId;

    @Transient
    private Long bindCode;

    @Transient
    private Long agentId;

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
}

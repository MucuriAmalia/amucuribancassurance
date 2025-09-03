package com.brokersystems.brokerapp.quotes.dto;

import java.util.List;

public class ComparisonQuotDTO {

    private Long quotId;
    private List<Long> contractId;

    public Long getQuotId() {
        return quotId;
    }

    public void setQuotId(Long quotId) {
        this.quotId = quotId;
    }

    public List<Long> getContractId() {
        return contractId;
    }

    public void setContractId(List<Long> contractId) {
        this.contractId = contractId;
    }
}
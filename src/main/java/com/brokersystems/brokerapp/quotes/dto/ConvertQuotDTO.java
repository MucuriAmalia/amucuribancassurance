package com.brokersystems.brokerapp.quotes.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class ConvertQuotDTO {

    private Long quotProdId;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date startDate;

    public Long getQuotProdId() {
        return quotProdId;
    }

    public void setQuotProdId(Long quotProdId) {
        this.quotProdId = quotProdId;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }
}

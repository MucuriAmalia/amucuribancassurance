package com.brokersystems.brokerapp.uw.model;

import java.util.Date;

/**
 * Created by peter on 6/7/2017.
 */
public class AdminFeeForm {

    private Long currencyId;

    private Long clientId;

    private Long brnCode;

    private String remarks;

    private Date processDate;

    public Long getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Long currencyId) {
        this.currencyId = currencyId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public Long getBrnCode() {
        return brnCode;
    }

    public void setBrnCode(Long brnCode) {
        this.brnCode = brnCode;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Date getProcessDate() {
        return processDate;
    }

    public void setProcessDate(Date processDate) {
        this.processDate = processDate;
    }
}

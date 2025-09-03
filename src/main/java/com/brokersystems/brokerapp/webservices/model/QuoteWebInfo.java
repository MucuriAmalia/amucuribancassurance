package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 3/30/2018.
 */
public class QuoteWebInfo {

    private String clientType;
    private String clientName;
    private String currency;
    private String wef;
    private String wet;
    private String quotNo;
    private String quotStatus;
    private String quotPrem;
    private String quotTaxes;
    private String quotExpDate;
    private String message;


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWef() {
        return wef;
    }

    public void setWef(String wef) {
        this.wef = wef;
    }

    public String getWet() {
        return wet;
    }

    public void setWet(String wet) {
        this.wet = wet;
    }

    public String getQuotNo() {
        return quotNo;
    }

    public void setQuotNo(String quotNo) {
        this.quotNo = quotNo;
    }

    public String getQuotStatus() {
        return quotStatus;
    }

    public void setQuotStatus(String quotStatus) {
        this.quotStatus = quotStatus;
    }

    public String getQuotPrem() {
        return quotPrem;
    }

    public void setQuotPrem(String quotPrem) {
        this.quotPrem = quotPrem;
    }

    public String getQuotTaxes() {
        return quotTaxes;
    }

    public void setQuotTaxes(String quotTaxes) {
        this.quotTaxes = quotTaxes;
    }

    public String getQuotExpDate() {
        return quotExpDate;
    }

    public void setQuotExpDate(String quotExpDate) {
        this.quotExpDate = quotExpDate;
    }
}

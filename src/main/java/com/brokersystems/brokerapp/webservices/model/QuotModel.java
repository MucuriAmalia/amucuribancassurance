package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 3/28/2018.
 */
public class QuotModel {

    private String wef;
    private String wet;
    private String status;
    private String quotNo;
    private String client;
    private String quotCode;

    public QuotModel() {
    }

    public QuotModel(String wef, String wet, String status, String quotNo, String client,String quotCode) {
        this.wef = wef;
        this.wet = wet;
        this.status = status;
        this.quotNo = quotNo;
        this.client = client;
        this.quotCode = quotCode;
    }

    public String getQuotCode() {
        return quotCode;
    }

    public void setQuotCode(String quotCode) {
        this.quotCode = quotCode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQuotNo() {
        return quotNo;
    }

    public void setQuotNo(String quotNo) {
        this.quotNo = quotNo;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}




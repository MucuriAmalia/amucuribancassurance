package com.brokersystems.brokerapp.medical.model;

/**
 * Created by HP on 8/13/2017.
 */
public class RequestLogBean {

    private String rejectShtDesc;
    private String rejectReason;

    public RequestLogBean(String rejectShtDesc, String rejectReason) {
        this.rejectShtDesc = rejectShtDesc;
        this.rejectReason = rejectReason;
    }

    public String getRejectShtDesc() {
        return rejectShtDesc;
    }

    public void setRejectShtDesc(String rejectShtDesc) {
        this.rejectShtDesc = rejectShtDesc;
    }

    public String getRejectReason() {
        return rejectReason;
    }

    public void setRejectReason(String rejectReason) {
        this.rejectReason = rejectReason;
    }
}

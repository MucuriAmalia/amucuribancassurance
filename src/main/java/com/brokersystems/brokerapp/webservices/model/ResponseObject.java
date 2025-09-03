package com.brokersystems.brokerapp.webservices.model;

public class ResponseObject {

    private boolean success;
    private String policyno;
    private String message;
    private Long refcode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPolicyno() {
        return policyno;
    }

    public void setPolicyno(String policyno) {
        this.policyno = policyno;
    }

    public Long getRefcode() {
        return refcode;
    }

    public void setRefcode(Long refcode) {
        this.refcode = refcode;
    }
}

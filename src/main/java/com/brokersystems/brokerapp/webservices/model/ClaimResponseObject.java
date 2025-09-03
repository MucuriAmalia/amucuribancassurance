package com.brokersystems.brokerapp.webservices.model;

public class ClaimResponseObject {

    private boolean success;
    private String claimno;
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

    public String getClaimno() {
        return claimno;
    }

    public void setClaimno(String claimno) {
        this.claimno = claimno;
    }

    public Long getRefcode() {
        return refcode;
    }

    public void setRefcode(Long refcode) {
        this.refcode = refcode;
    }
}

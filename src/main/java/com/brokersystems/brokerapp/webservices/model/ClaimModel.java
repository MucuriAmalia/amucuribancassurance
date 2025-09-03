package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 3/28/2018.
 */
public class ClaimModel {

    private String claimNo;
    private String insured;
    private String risk;
    private String lossDate;
    private String status;
    private String clmId;
    private String lossDesc;
    private String riskIdentifier;
    private String bookedDate;


    public ClaimModel() {
    }

    public ClaimModel(String claimNo, String insured, String risk, String lossDate, String status,String clmId,String lossDesc, String riskIdentifier,String bookedDate) {
        this.claimNo = claimNo;
        this.insured = insured;
        this.risk = risk;
        this.lossDate = lossDate;
        this.status = status;
        this.clmId = clmId;
        this.lossDesc = lossDesc;
        this.riskIdentifier = riskIdentifier;
        this.bookedDate = bookedDate;
    }

    public String getClmId() {
        return clmId;
    }

    public void setClmId(String clmId) {
        this.clmId = clmId;
    }

    public String getClaimNo() {
        return claimNo;
    }

    public void setClaimNo(String claimNo) {
        this.claimNo = claimNo;
    }

    public String getInsured() {
        return insured;
    }

    public void setInsured(String insured) {
        this.insured = insured;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getLossDate() {
        return lossDate;
    }

    public void setLossDate(String lossDate) {
        this.lossDate = lossDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLossDesc() {
        return lossDesc;
    }

    public void setLossDesc(String lossDesc) {
        this.lossDesc = lossDesc;
    }
    public String getRiskIdentifier() {return riskIdentifier;}

    public void setRiskIdentifier(String riskIdentifier) {
        this.riskIdentifier = riskIdentifier;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }
}

package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 3/30/2018.
 */

public class RisksWebInfo {

     private String insured;
     private String riskId;
     private String riskDesc;
     private String wef;
     private String wet;
     private String coverType;
     private String premium;
    private String sectionName;
    private String sectionValue;
    private String sectionRate;
    private String sectionPremium;
    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInsured() {
        return insured;
    }

    public void setInsured(String insured) {
        this.insured = insured;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
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

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionValue() {
        return sectionValue;
    }

    public void setSectionValue(String sectionValue) {
        this.sectionValue = sectionValue;
    }

    public String getSectionRate() {
        return sectionRate;
    }

    public void setSectionRate(String sectionRate) {
        this.sectionRate = sectionRate;
    }

    public String getSectionPremium() {
        return sectionPremium;
    }

    public void setSectionPremium(String sectionPremium) {
        this.sectionPremium = sectionPremium;
    }
}

package com.brokersystems.brokerapp.webservices.model;

import com.brokersystems.brokerapp.setup.dto.ClientDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class RiskData {

    private String riskShtDesc;
    private String riskDesc;
    private String coverType;
    private BigDecimal sumInsured;
    private BigDecimal windscreen;
    private BigDecimal radio;
    private ClientDTO insured;
    private BigDecimal premium;

    private VehicleDetails vehicle;
    private List<UwDocsDTO> documents;

    public List<UwDocsDTO> getDocuments() {
        return documents;
    }

    public void setDocuments(List<UwDocsDTO> documents) {
        this.documents = documents;
    }

    public VehicleDetails getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDetails vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getRiskShtDesc() {
        return riskShtDesc;
    }

    public void setRiskShtDesc(String riskShtDesc) {
        this.riskShtDesc = riskShtDesc;
    }


    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getWindscreen() {
        return windscreen;
    }

    public void setWindscreen(BigDecimal windscreen) {
        this.windscreen = windscreen;
    }

    public BigDecimal getRadio() {
        return radio;
    }

    public void setRadio(BigDecimal radio) {
        this.radio = radio;
    }

    public ClientDTO getInsured() {
        return insured;
    }

    public void setInsured(ClientDTO insured) {
        this.insured = insured;
    }
}

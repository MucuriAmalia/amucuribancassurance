package com.brokersystems.brokerapp.webservices.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HP on 3/30/2018.
 */
public class PolicyWebInfo {

    private String polNo;
    private String polRevNo;
    private String wefDate;
    private String wetDate;
    private String renewalDate;
    private String insurance;
    private BigDecimal commission;
    private String currency;
    private String product;
    private String authStatus;
    private String authDate;
    private String refNo;
    private BigDecimal premium;
    private BigDecimal sumInsured;
    private BigDecimal taxes;
    private String message;
    private List<VehicleDTO> vehicleDetails;

    public List<VehicleDTO> getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(List<VehicleDTO> vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPolNo() {
        return polNo;
    }

    public void setPolNo(String polNo) {
        this.polNo = polNo;
    }

    public String getPolRevNo() {
        return polRevNo;
    }

    public void setPolRevNo(String polRevNo) {
        this.polRevNo = polRevNo;
    }

    public String getWefDate() {
        return wefDate;
    }

    public void setWefDate(String wefDate) {
        this.wefDate = wefDate;
    }

    public String getWetDate() {
        return wetDate;
    }

    public void setWetDate(String wetDate) {
        this.wetDate = wetDate;
    }

    public String getRenewalDate() {
        return renewalDate;
    }

    public void setRenewalDate(String renewalDate) {
        this.renewalDate = renewalDate;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getAuthDate() {
        return authDate;
    }

    public void setAuthDate(String authDate) {
        this.authDate = authDate;
    }

    public String getRefNo() {
        return refNo;
    }

    public void setRefNo(String refNo) {
        this.refNo = refNo;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getTaxes() {
        return taxes;
    }

    public void setTaxes(BigDecimal taxes) {
        this.taxes = taxes;
    }
}

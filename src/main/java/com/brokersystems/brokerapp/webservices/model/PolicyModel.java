package com.brokersystems.brokerapp.webservices.model;

import java.util.List;

/**
 * Created by HP on 3/28/2018.
 */
public class PolicyModel {

    private String wef;
    private String wet;
    private String status;
    private String balance;
    private String premium;
    private String insuranceCompany;
    private String policyNo;
    private String product;
    private String client;
    private String policyId;

    private String registrationNumber;
    private List<VehicleDTO> vehicleDetails;

    public PolicyModel() {
    }

    public PolicyModel(String wef, String wet, String status, String balance, String insuranceCompany, String policyNo, String product, String client,String policyId,
                       String registrationNumber, List<VehicleDTO> vehicleDetails, String premium) {
        this.wef = wef;
        this.wet = wet;
        this.status = status;
        this.balance = balance;
        this.insuranceCompany = insuranceCompany;
        this.policyNo = policyNo;
        this.product = product;
        this.client = client;
        this.policyId  = policyId;
        this.registrationNumber = registrationNumber;
        this.vehicleDetails = vehicleDetails;
        this.premium = premium;
    }



    public List<VehicleDTO> getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(List<VehicleDTO> vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId(String policyId) {
        this.policyId = policyId;
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

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getInsuranceCompany() {
        return insuranceCompany;
    }

    public void setInsuranceCompany(String insuranceCompany) {
        this.insuranceCompany = insuranceCompany;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }
}

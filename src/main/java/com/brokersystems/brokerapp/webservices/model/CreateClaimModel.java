package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 3/28/2018.
 */
public class CreateClaimModel {

    private String registrationNumber;
    private String lossDate;
    private String lossDesc;
    private String notificationDate;


    public CreateClaimModel() {
    }

    public CreateClaimModel(String registrationNumber, String lossDate, String lossDesc, String notificationDate) {
        this.registrationNumber = registrationNumber;
        this.lossDate = lossDate;
        this.lossDesc = lossDesc;
        this.notificationDate = notificationDate;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getLossDate() {
        return lossDate;
    }

    public void setLossDate(String lossDate) {
        this.lossDate = lossDate;
    }

    public String getLossDesc() {
        return lossDesc;
    }

    public void setLossDesc(String lossDesc) {
        this.lossDesc = lossDesc;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }
}

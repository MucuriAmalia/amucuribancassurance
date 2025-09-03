package com.brokersystems.brokerapp.aki.dto;

public class VerifyErrorCertificate {

    private String IssuanceRequestID;
    private boolean IsApproved;
    private boolean IsLogBookVerified;
    private boolean IsVehicleInspected;
    private String AdditionalComments;
    private String UserName;

    public String getIssuanceRequestID() {
        return IssuanceRequestID;
    }

    public void setIssuanceRequestID(String issuanceRequestID) {
        IssuanceRequestID = issuanceRequestID;
    }

    public boolean getIsApproved() {
        return IsApproved;
    }

    public void setIsApproved(boolean approved) {
        IsApproved = approved;
    }

    public boolean getIsLogBookVerified() {
        return IsLogBookVerified;
    }

    public void setIsLogBookVerified(boolean logBookVerified) {
        IsLogBookVerified = logBookVerified;
    }

    public boolean getIsVehicleInspected() {
        return IsVehicleInspected;
    }

    public void setIsVehicleInspected(boolean vehicleInspected) {
        IsVehicleInspected = vehicleInspected;
    }

    public String getAdditionalComments() {
        return AdditionalComments;
    }

    public void setAdditionalComments(String additionalComments) {
        AdditionalComments = additionalComments;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}

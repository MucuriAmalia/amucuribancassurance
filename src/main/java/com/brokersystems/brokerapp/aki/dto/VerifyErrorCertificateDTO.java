package com.brokersystems.brokerapp.aki.dto;

public class VerifyErrorCertificateDTO {

    private String IssuanceRequestID;
    private boolean IsApproved;
    private boolean IsLogBookVerified;
    private boolean IsVehicleInspected;
    private String AdditionalComments;
    private String UserName;
    private Long ipucode;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Long getIpucode() {
        return ipucode;
    }

    public void setIpucode(Long ipucode) {
        this.ipucode = ipucode;
    }

    public String getIssuanceRequestID() {
        return IssuanceRequestID;
    }

    public void setIssuanceRequestID(String issuanceRequestID) {
        IssuanceRequestID = issuanceRequestID;
    }

    public boolean isApproved() {
        return IsApproved;
    }

    public void setApproved(boolean approved) {
        IsApproved = approved;
    }

    public boolean isLogBookVerified() {
        return IsLogBookVerified;
    }

    public void setLogBookVerified(boolean logBookVerified) {
        IsLogBookVerified = logBookVerified;
    }

    public boolean isVehicleInspected() {
        return IsVehicleInspected;
    }

    public void setVehicleInspected(boolean vehicleInspected) {
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

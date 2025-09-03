package com.brokersystems.brokerapp.webservices.portalmodel;

import java.util.Date;

/**
 * Created by HP on 6/12/2018.
 */
public class Agent {

    private String name;
    private String shtDesc;
    private String telNo;
    private String postalAddress;
    private String postalCode;
    private String pin;
    private String email;
    private String physicalAddress;
    private String idNumber;
    private String licenseNumber;
    private String dob;
    private String gender;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "Agent{" +
                "name='" + name + '\'' +
                ", shtDesc='" + shtDesc + '\'' +
                ", telNo='" + telNo + '\'' +
                ", postalAddress='" + postalAddress + '\'' +
                ", postalCode='" + postalCode + '\'' +
                ", pin='" + pin + '\'' +
                ", email='" + email + '\'' +
                ", physicalAddress='" + physicalAddress + '\'' +
                ", idNumber='" + idNumber + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                '}';
    }
}

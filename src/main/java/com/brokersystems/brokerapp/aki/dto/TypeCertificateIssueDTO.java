package com.brokersystems.brokerapp.aki.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class TypeCertificateIssueDTO {
    private String IntermediaryIRANumber;

    private Integer Typeofcover;

    private String Policyholder;

    private String policynumber;

    private String Commencingdate;

    private String Expiringdate;

    private String Registrationnumber;

    private String Chassisnumber;

    private String Phonenumber;

    private String Bodytype;

    private String Vehiclemake;

    private String Vehiclemodel;

    private String Enginenumber;

    private String Email;

    private BigDecimal SumInsured;

    private String InsuredPIN;

    private Integer Yearofmanufacture;

    private Integer Licensedtocarry;

    private Integer Tonnage;

    private Integer TypeOfCertificate;

    private String HudumaNumber;

    private Integer Membercompanyid;

    private Long ipucode;

    private String user;

    private Integer VehicleType;

    private Integer TonnageCarryingCapacity;

    public Integer getMembercompanyid() {
        return Membercompanyid;
    }

    public void setMembercompanyid(Integer membercompanyid) {
        Membercompanyid = membercompanyid;
    }

    public Long getIpucode() {
        return ipucode;
    }

    public void setIpucode(Long ipucode) {
        this.ipucode = ipucode;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getTonnageCarryingCapacity() {
        return TonnageCarryingCapacity;
    }

    public void setTonnageCarryingCapacity(Integer tonnageCarryingCapacity) {
        TonnageCarryingCapacity = tonnageCarryingCapacity;
    }

    public String getIntermediaryIRANumber() {
        return IntermediaryIRANumber;
    }

    public void setIntermediaryIRANumber(String intermediaryIRANumber) {
        IntermediaryIRANumber = intermediaryIRANumber;
    }

    public Integer getTypeofcover() {
        return Typeofcover;
    }

    public void setTypeofcover(Integer typeofcover) {
        Typeofcover = typeofcover;
    }

    public String getPolicyholder() {
        return Policyholder;
    }

    public void setPolicyholder(String policyholder) {
        Policyholder = policyholder;
    }

    public String getPolicynumber() {
        return policynumber;
    }

    public void setPolicynumber(String policynumber) {
        this.policynumber = policynumber;
    }

    public String getCommencingdate() {
        return Commencingdate;
    }

    public void setCommencingdate(String commencingdate) {
        Commencingdate = commencingdate;
    }

    public String getExpiringdate() {
        return Expiringdate;
    }

    public void setExpiringdate(String expiringdate) {
        Expiringdate = expiringdate;
    }

    public String getRegistrationnumber() {
        return Registrationnumber;
    }

    public void setRegistrationnumber(String registrationnumber) {
        Registrationnumber = registrationnumber;
    }

    public String getChassisnumber() {
        return Chassisnumber;
    }

    public void setChassisnumber(String chassisnumber) {
        Chassisnumber = chassisnumber;
    }

    public String getPhonenumber() {
        return Phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        Phonenumber = phonenumber;
    }

    public String getBodytype() {
        return Bodytype;
    }

    public void setBodytype(String bodytype) {
        Bodytype = bodytype;
    }

    public String getVehiclemake() {
        return Vehiclemake;
    }

    public void setVehiclemake(String vehiclemake) {
        Vehiclemake = vehiclemake;
    }

    public String getVehiclemodel() {
        return Vehiclemodel;
    }

    public void setVehiclemodel(String vehiclemodel) {
        Vehiclemodel = vehiclemodel;
    }

    public String getEnginenumber() {
        return Enginenumber;
    }

    public void setEnginenumber(String enginenumber) {
        Enginenumber = enginenumber;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public BigDecimal getSumInsured() {
        return SumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        SumInsured = sumInsured;
    }

    public String getInsuredPIN() {
        return InsuredPIN;
    }

    public void setInsuredPIN(String insuredPIN) {
        InsuredPIN = insuredPIN;
    }

    public Integer getYearofmanufacture() {
        return Yearofmanufacture;
    }

    public void setYearofmanufacture(Integer yearofmanufacture) {
        Yearofmanufacture = yearofmanufacture;
    }

    public Integer getLicensedtocarry() {
        return Licensedtocarry;
    }

    public void setLicensedtocarry(Integer licensedtocarry) {
        Licensedtocarry = licensedtocarry;
    }



    public Integer getTonnage() {
        return Tonnage;
    }

    public void setTonnage(Integer tonnage) {
        Tonnage = tonnage;
    }

    public Integer getTypeOfCertificate() {
        return TypeOfCertificate;
    }

    public void setTypeOfCertificate(Integer typeOfCertificate) {
        TypeOfCertificate = typeOfCertificate;
    }

    public String getHudumaNumber() {
        return HudumaNumber;
    }

    public void setHudumaNumber(String hudumaNumber) {
        HudumaNumber = hudumaNumber;
    }

    public Integer getVehicleType() {
        return VehicleType;
    }

    public void setVehicleType(Integer vehicleType) {
        VehicleType = vehicleType;
    }

    @Override
    public String toString() {
        return "TypeCertificateIssueDTO [IntermediaryIRANumber=" + IntermediaryIRANumber + ", Typeofcover="
                + Typeofcover + ", Policyholder=" + Policyholder + ", policynumber=" + policynumber
                + ", Commencingdate=" + Commencingdate + ", Expiringdate=" + Expiringdate + ", Registrationnumber="
                + Registrationnumber + ", Chassisnumber=" + Chassisnumber + ", Phonenumber=" + Phonenumber
                + ", Bodytype=" + Bodytype + ", Vehiclemake=" + Vehiclemake + ", Vehiclemodel=" + Vehiclemodel
                + ", Enginenumber=" + Enginenumber + ", Email=" + Email + ", SumInsured=" + SumInsured + ", InsuredPIN="
                + InsuredPIN + ", Yearofmanufacture=" + Yearofmanufacture + ", Licensedtocarry=" + Licensedtocarry
                + ", Tonnage=" + Tonnage + ", TypeOfCertificate=" + TypeOfCertificate
                + ", HudumaNumber=" + HudumaNumber + ", VehicleType=" + VehicleType + "]";
    }


}

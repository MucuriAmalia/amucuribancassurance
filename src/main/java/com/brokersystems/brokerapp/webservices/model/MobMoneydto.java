package com.brokersystems.brokerapp.webservices.model;

/**
 * Created by HP on 5/27/2018.
 */
public class MobMoneydto {


    private   String transId ;
    private   String transTime;
    private  String amount;
    private String billRfNumber;
    private String phoneNumber;
    private String clientFname;
    private  String MiddleName;
    private String lname;



    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTransTime() {
        return transTime;
    }

    public void setTransTime(String transTime) {
        this.transTime = transTime;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBillRfNumber() {
        return billRfNumber;
    }

    public void setBillRfNumber(String billRfNumber) {
        this.billRfNumber = billRfNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getClientFname() {
        return clientFname;
    }

    public void setClientFname(String clientFname) {
        this.clientFname = clientFname;
    }

    public String getMiddleName() {
        return MiddleName;
    }

    public void setMiddleName(String middleName) {
        MiddleName = middleName;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }
}

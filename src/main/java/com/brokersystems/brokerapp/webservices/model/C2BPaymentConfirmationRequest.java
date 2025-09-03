package com.brokersystems.brokerapp.webservices.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by HP on 4/3/2018.
 */
public class C2BPaymentConfirmationRequest {

    private String trans_time;
    private String trans_amount;
    private String trans_type;
    private String msisdn;
    private String invoive_number;
    private  String paybill_number;
    private String trans_id;
    private String account_number;

    public String getTrans_time() {
        return trans_time;
    }

    public void setTrans_time(String trans_time) {
        this.trans_time = trans_time;
    }

    public String getTrans_amount() {
        return trans_amount;
    }

    public void setTrans_amount(String trans_amount) {
        this.trans_amount = trans_amount;
    }

    public String getTrans_type() {
        return trans_type;
    }

    public void setTrans_type(String trans_type) {
        this.trans_type = trans_type;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getInvoive_number() {
        return invoive_number;
    }

    public void setInvoive_number(String invoive_number) {
        this.invoive_number = invoive_number;
    }

    public String getPaybill_number() {
        return paybill_number;
    }

    public void setPaybill_number(String paybill_number) {
        this.paybill_number = paybill_number;
    }

    public String getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(String trans_id) {
        this.trans_id = trans_id;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    @Override
    public String toString() {
        return "C2BPaymentConfirmationRequest{" +
                "trans_time='" + trans_time + '\'' +
                ", trans_amount='" + trans_amount + '\'' +
                ", trans_type='" + trans_type + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", invoive_number='" + invoive_number + '\'' +
                ", paybill_number='" + paybill_number + '\'' +
                ", trans_id='" + trans_id + '\'' +
                ", account_number='" + account_number + '\'' +
                '}';
    }
}

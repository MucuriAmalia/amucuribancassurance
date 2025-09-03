package com.brokersystems.brokerapp.trans.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by HP on 5/27/2018.
 */
@Entity
@Table(name="sys_brk_integration_dtls")
public class IntegrationDtls {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="bid_id")
    private Long bidId;

    @Column(name="bid_trans_code",unique = true,nullable = false,length = 30)
    private   String transId ;

    @Column(name="bid_trans_time",nullable = false)
    private Date transTime;

    @Column(name="bid_trans_amt",nullable = false)
    private BigDecimal transAmount;

    @Column(name="bid_bill_ref_no",length = 70)
    private String billRfNumber;

    @Column(name="bid_phone_no",length = 50,nullable = false)
    private String phoneNumber;

    @Column(name="bid_client_fname",nullable = false)
    private String clientFname;


    @Column(name="bid_client_mname")
    private  String MiddleName;

    @Column(name="bid_client_lname")
    private String lname;

    @Column(name="bid_receipted",length = 1,nullable = false)
    private String receipted;

    @Column(name="bid_receipted_dt")
    private Date receiptedDate;

    @Column(name="bid_receipted_user")
    private String receiptedUser;

    @Column(name="bid_new_ref_no",length = 70)
    private String billNewRfNumber;


    public Long getBidId() {
        return bidId;
    }

    public void setBidId(Long bidId) {
        this.bidId = bidId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public Date getTransTime() {
        return transTime;
    }

    public void setTransTime(Date transTime) {
        this.transTime = transTime;
    }

    public BigDecimal getTransAmount() {
        return transAmount;
    }

    public void setTransAmount(BigDecimal transAmount) {
        this.transAmount = transAmount;
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

    public String getReceipted() {
        return receipted;
    }

    public void setReceipted(String receipted) {
        this.receipted = receipted;
    }

    public Date getReceiptedDate() {
        return receiptedDate;
    }

    public void setReceiptedDate(Date receiptedDate) {
        this.receiptedDate = receiptedDate;
    }

    public String getReceiptedUser() {
        return receiptedUser;
    }

    public void setReceiptedUser(String receiptedUser) {
        this.receiptedUser = receiptedUser;
    }

    public String getBillNewRfNumber() {
        return billNewRfNumber;
    }

    public void setBillNewRfNumber(String billNewRfNumber) {
        this.billNewRfNumber = billNewRfNumber;
    }
}

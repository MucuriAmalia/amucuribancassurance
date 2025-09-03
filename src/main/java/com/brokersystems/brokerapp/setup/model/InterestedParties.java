package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by HP on 9/13/2017.
 */
@Entity
@Table(name="sys_brk_int_parties")
@ToString
public class InterestedParties {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="part_code")
    private Long partCode;

    @Column(name="part_name",nullable = false)
    private String partName;

    @Column(name="part_type",nullable = false)
    private String partType;

    @Column(name="part_pin",nullable = false)
    private String pinNumber;

    @Column(name="part_post_address")
    private String postalAddress;

    @Column(name="part_email_address")
    private String emailAddress;

    @Column(name="part_tel_no")
    private String telNo;

    @Column(name="part_date_reg")
    @Temporal(TemporalType.DATE)
    private Date dateRegistered;

    @Column(name="part_reg_no",nullable = false)
    private String regNo;

    @Column(name="part_dt_created")
    private Date dateCreated;

    @ManyToOne
    @JoinColumn(name="part_user_creared",nullable=false)
    private User user;

    public Long getPartCode() {
        return partCode;
    }

    public void setPartCode(Long partCode) {
        this.partCode = partCode;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getPartType() {
        return partType;
    }

    public void setPartType(String partType) {
        this.partType = partType;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

    public Date getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(Date dateRegistered) {
        this.dateRegistered = dateRegistered;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPinNumber() {
        return pinNumber;
    }

    public void setPinNumber(String pinNumber) {
        this.pinNumber = pinNumber;
    }

    public String getRegNo() {
        return regNo;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InterestedParties that = (InterestedParties) o;

        return partCode.equals(that.partCode);

    }

    @Override
    public int hashCode() {
        return partCode.hashCode();
    }
}

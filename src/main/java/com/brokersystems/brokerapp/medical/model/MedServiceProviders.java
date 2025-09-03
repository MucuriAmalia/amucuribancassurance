package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.accounts.model.BankBranches;
import com.brokersystems.brokerapp.setup.model.Country;
import com.brokersystems.brokerapp.setup.model.PaymentModes;

import javax.persistence.*;

/**
 * Created by peter on 4/26/2017.
 */
@Entity
@Table(name="sys_brk_med_serv_prvds")
public class MedServiceProviders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "msp_id")
    private Long mspId;

    @Column(name = "msp_name",nullable = false)
    private String name;

    @Column(name = "msp_cont_person",nullable = false)
    private String contactPerson;

    @Column(name = "msp_cont_tel")
    private String contactTelNo;

    @Column(name = "msp_post_addr")
    private String postalAddress;

    @Column(name = "msp_post_code")
    private String postalCode;

    @Column(name = "msp_phy_address")
    private String physicalAddress;

    @ManyToOne
    @JoinColumn(name="msp_cou_code")
    private Country country;

    @Column(name = "msp_pin",nullable = false)
    private String pinNo;

    @Column(name = "msp_tel_no")
    private String telNumber;

    @Column(name = "msp_email")
    private String email;

    @ManyToOne
    @JoinColumn(name="msp_spt_id")
    private MedServiceProviderTypes serviceProviderTypes;

    @Column(name = "msp_status",nullable = false)
    private String status;

    @Column(name = "msp_smart_enable")
    private boolean smartEnabled;

    @Column(name = "msp_blacklisted")
    private boolean blacklisted;

    @Column(name = "msp_accredited")
    private boolean accredited;

    @Column(name = "msp_payee")
    private String payeeName;

    @ManyToOne
    @JoinColumn(name="msp_bbr_id")
    private BankBranches bankBranches;

    @Column(name = "msp_account")
    private String accountNumber;

    @ManyToOne
    @JoinColumn(name="msp_pm_id")
    private PaymentModes paymentModes;


    public Long getMspId() {
        return mspId;
    }

    public void setMspId(Long mspId) {
        this.mspId = mspId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactTelNo() {
        return contactTelNo;
    }

    public void setContactTelNo(String contactTelNo) {
        this.contactTelNo = contactTelNo;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhysicalAddress() {
        return physicalAddress;
    }

    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public String getPinNo() {
        return pinNo;
    }

    public void setPinNo(String pinNo) {
        this.pinNo = pinNo;
    }

    public String getTelNumber() {
        return telNumber;
    }

    public void setTelNumber(String telNumber) {
        this.telNumber = telNumber;
    }

    public MedServiceProviderTypes getServiceProviderTypes() {
        return serviceProviderTypes;
    }

    public void setServiceProviderTypes(MedServiceProviderTypes serviceProviderTypes) {
        this.serviceProviderTypes = serviceProviderTypes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSmartEnabled() {
        return smartEnabled;
    }

    public void setSmartEnabled(boolean smartEnabled) {
        this.smartEnabled = smartEnabled;
    }

    public String getPayeeName() {
        return payeeName;
    }

    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    public BankBranches getBankBranches() {
        return bankBranches;
    }

    public void setBankBranches(BankBranches bankBranches) {
        this.bankBranches = bankBranches;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public PaymentModes getPaymentModes() {
        return paymentModes;
    }

    public void setPaymentModes(PaymentModes paymentModes) {
        this.paymentModes = paymentModes;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isBlacklisted() {
        return blacklisted;
    }

    public void setBlacklisted(boolean blacklisted) {
        this.blacklisted = blacklisted;
    }

    public boolean isAccredited() {
        return accredited;
    }

    public void setAccredited(boolean accredited) {
        this.accredited = accredited;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedServiceProviders providers = (MedServiceProviders) o;

        return mspId != null ? mspId.equals(providers.mspId) : providers.mspId == null;

    }

    @Override
    public int hashCode() {
        return mspId != null ? mspId.hashCode() : 0;
    }
}

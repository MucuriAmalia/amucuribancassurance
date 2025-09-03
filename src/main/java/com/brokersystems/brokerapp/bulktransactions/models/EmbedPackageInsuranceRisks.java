package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.ClientTypes;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_embed_package_insurance_risks")
public class EmbedPackageInsuranceRisks extends AuditBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "risk_embed_id")
    private Long riskEmbedId;

    @Column(name = "risk_serial_no")
    private String serialNo;

    @Column(name = "risk_insured_fname")
    private String insuredFname;

    @Column(name = "insured_other_names")
    private String insuredOthernames;

    @Column(name = "risk_insured_id")
    private String insuredID;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="risk_insured_type")
    private ClientTypes insuredType;

    @Column(name = "risk_insured_category")
    private String insuredCategory;

    @Column(name = "insured_account_no")
    private String insuredAcc;

    @Column(name = "risk_premium_amt")
    private BigDecimal riskPremium;

    @Column(name = "risk_premium_item")
    private String premItem;

    @Column(name = "risk_status")
    private String riskStatus;

    @Column(name = "risk_code")
    private String riskCode;

    @Column(name = "risk_isured_dob")
    private Date insuredDOB;

    @Column(name = "risk_insured_pin")
    private String insuredPin;

    @Column(name = "risk_insured_phone")
    private String insuredPhone;

    @Column(name = "risk_isured_email")
    private String insuredEmail;

    @Column(name = "risk_einsured_cif")
    private String insuredCIF;

    @Column(name = "risk_month")
    private String riskMonth;

    @Column(name = "risk_acc_open_date")
    private Date accOpenDate;

    @Column(name = "risk_upload_date")
    private Date uploadDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "embed_package_id")
    private EmbedPackageInsurance embedPackageInsurance;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="embed_insured_type")
    private ClientTypes tenantType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "risk_isnured_branch")
    private OrgBranch insuredBranch;

    public ClientTypes getInsuredType() {
        return insuredType;
    }

    public void setInsuredType(ClientTypes insuredType) {
        this.insuredType = insuredType;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public String getRiskCode() {
        return riskCode;
    }

    public void setRiskCode(String riskCode) {
        this.riskCode = riskCode;
    }

    public OrgBranch getInsuredBranch() {
        return insuredBranch;
    }

    public void setInsuredBranch(OrgBranch insuredBranch) {
        this.insuredBranch = insuredBranch;
    }

    public Long getRiskEmbedId() {
        return riskEmbedId;
    }

    public void setRiskEmbedId(Long riskEmbedId) {
        this.riskEmbedId = riskEmbedId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getInsuredFname() {
        return insuredFname;
    }

    public void setInsuredFname(String insuredFname) {
        this.insuredFname = insuredFname;
    }

    public String getInsuredOthernames() {
        return insuredOthernames;
    }

    public void setInsuredOthernames(String insuredOthernames) {
        this.insuredOthernames = insuredOthernames;
    }

    public String getInsuredID() {
        return insuredID;
    }

    public void setInsuredID(String insuredID) {
        this.insuredID = insuredID;
    }

    public String getInsuredCategory() {
        return insuredCategory;
    }

    public void setInsuredCategory(String insuredCategory) {
        this.insuredCategory = insuredCategory;
    }

    public String getInsuredAcc() {
        return insuredAcc;
    }

    public void setInsuredAcc(String insuredAcc) {
        this.insuredAcc = insuredAcc;
    }

    public BigDecimal getRiskPremium() {
        return riskPremium;
    }

    public void setRiskPremium(BigDecimal riskPremium) {
        this.riskPremium = riskPremium;
    }

    public String getPremItem() {
        return premItem;
    }

    public void setPremItem(String premItem) {
        this.premItem = premItem;
    }

    public Date getInsuredDOB() {
        return insuredDOB;
    }

    public void setInsuredDOB(Date insuredDOB) {
        this.insuredDOB = insuredDOB;
    }

    public String getInsuredPin() {
        return insuredPin;
    }

    public void setInsuredPin(String insuredPin) {
        this.insuredPin = insuredPin;
    }

    public String getInsuredPhone() {
        return insuredPhone;
    }

    public void setInsuredPhone(String insuredPhone) {
        this.insuredPhone = insuredPhone;
    }

    public String getInsuredEmail() {
        return insuredEmail;
    }

    public void setInsuredEmail(String insuredEmail) {
        this.insuredEmail = insuredEmail;
    }

    public String getInsuredCIF() {
        return insuredCIF;
    }

    public void setInsuredCIF(String insuredCIF) {
        this.insuredCIF = insuredCIF;
    }

    public String getRiskMonth() {
        return riskMonth;
    }

    public void setRiskMonth(String riskMonth) {
        this.riskMonth = riskMonth;
    }

    public Date getAccOpenDate() {
        return accOpenDate;
    }

    public void setAccOpenDate(Date accOpenDate) {
        this.accOpenDate = accOpenDate;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public EmbedPackageInsurance getEmbedPackageFirstAssurance() {
        return embedPackageInsurance;
    }

    public void setEmbedPackageFirstAssurance(EmbedPackageInsurance embedPackageInsurance) {
        this.embedPackageInsurance = embedPackageInsurance;
    }

    public ClientTypes getTenantType() {
        return tenantType;
    }

    public void setTenantType(ClientTypes tenantType) {
        this.tenantType = tenantType;
    }
}

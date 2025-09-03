package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_group_life_risks")
public class GroupLifeRisks extends AuditBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "risk_life_id")
    private Long riskLifeId;

    @Column(name = "risks_serial_no")
    private String serialNo;

    @Column(name = "risks_insured_fname")
    private String insuredFName;

    @Column(name = "risks_insured_othernames")
    private String insuredOtherNames;

    @Column(name = "risks_insured_cif")
    private String insuredCIF;

    @Column(name = "risks_insured_dob")
    private Date insuredDOB;

    @Column(name = "risks_insured_pin")
    private String insuredPin;

    @Column(name = "insured_phone")
    private String insuredPhone;

    @Column(name = "insured_email")
    private String insuredEmail;

    @Column(name = "insured_id")
    private String insuredID;

    @Column(name = "risks_trans_date")
    private Date transDate;

    @Column(name = "risks_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "risks_premium_amount")
    private BigDecimal risksPremium;

    @Column(name = "risks_upload_date")
    private Date uploadedDate;

    @Column(name = "risks_processed_date")
    private Date processedDate;

    @Column(name = "risks_trans_status")
    private String transStatus;

    @Column(name = "risks_trans_code")
    private String transCode;

    @Column(name = "risks_employee_code")
    private String employeeCode;

    @Column(name = "risks_trans_type")
    private String transType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="risks_insured_type")
    private ClientTypes insuredType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "risks_insurer_code")
    private AccountDef insurerCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "risks_processed_by")
    private User processedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "risks_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "risks_insured_branch")
    private OrgBranch insuredBranch;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "risk_life_pol_id")
    private PolicyTrans policyTrans;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "group_life_id")
    private GroupLife groupLife;

    public Long getRiskLifeId() {
        return riskLifeId;
    }

    public void setRiskLifeId(Long riskLifeId) {
        this.riskLifeId = riskLifeId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getInsuredFName() {
        return insuredFName;
    }

    public void setInsuredFName(String insuredFName) {
        this.insuredFName = insuredFName;
    }

    public String getInsuredOtherNames() {
        return insuredOtherNames;
    }

    public void setInsuredOtherNames(String insuredOtherNames) {
        this.insuredOtherNames = insuredOtherNames;
    }

    public String getInsuredCIF() {
        return insuredCIF;
    }

    public void setInsuredCIF(String insuredCIF) {
        this.insuredCIF = insuredCIF;
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

    public String getInsuredID() {
        return insuredID;
    }

    public void setInsuredID(String insuredID) {
        this.insuredID = insuredID;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getRisksPremium() {
        return risksPremium;
    }

    public void setRisksPremium(BigDecimal risksPremium) {
        this.risksPremium = risksPremium;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date uploadedDate) {
        this.uploadedDate = uploadedDate;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public String getTransStatus() {
        return transStatus;
    }

    public void setTransStatus(String transStatus) {
        this.transStatus = transStatus;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public ClientTypes getInsuredType() {
        return insuredType;
    }

    public void setInsuredType(ClientTypes insuredType) {
        this.insuredType = insuredType;
    }

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public OrgBranch getInsuredBranch() {
        return insuredBranch;
    }

    public void setInsuredBranch(OrgBranch insuredBranch) {
        this.insuredBranch = insuredBranch;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public GroupLife getGroupLife() {
        return groupLife;
    }

    public void setGroupLife(GroupLife groupLife) {
        this.groupLife = groupLife;
    }
}

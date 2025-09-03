package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_trans_process_risks")
public class TransProcessRisks extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "trans_risk_id")
    private Long riskId;

    @Column(name = "trans_risk_serial_no")
    private String riskSerialNo;

    @Column(name = "trans_risk_insured_pin")
    private String insuredPin;

    @Column(name = "trans_risk_code")
    private String riskShtDesc;

    @Column(name = "trans_risk_desc")
    private String riskDesc;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "trans_risk_uploaded_by")
    private User riskUploadedBy;

    @Column(name = "trans_risk_upload_date")
    private Date riskUploadDate;

    @Column(name = "trans_risk_insured_fname")
    private String insuredFName;

    @Column(name = "trans_risk_insured_other_names")
    private String insuredOtherNames;

    @Column(name = "trans_risk_insured_phone_no")
    private String insuredPhoneNo;

    @Column(name = "trans_risk_insured_email")
    private String insuredEmail;

    @Column(name = "trans_risk_insured_id_no")
    private String insuredIdNo;

    @Column(name = "trans_risk_insured_type")
    private String insuredType;

    @Column(name = "trans_risk_insured_dob")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date insuredDOB;

    @Column(name = "trans_risk_insured_cif")
    private String insuredCIF;

    @Column(name = "trans_risk_wef")
    private Date riskWef;

    @Column(name = "trans_risk_wet")
    private Date riskWet;

    @Column(name="trans_risk_total_insts")
    private Integer riskTotalInsts;

    @Column(name = "trans_risk_risk_status")
    private String riskStatus;

    @Column(name = "trans_risk_ref_code")
    private String riskRefCode;

    @Column(name = "trans_risk_neg_premium")
    private BigDecimal negotiatedPrem;

    @ManyToOne
    @JoinColumn(name = "trans_processing_id", nullable = false)
    private TransactionProcessing transProcessing;

    public TransactionProcessing getTransProcessing() {
        return transProcessing;
    }

    public void setTransProcessing(TransactionProcessing transProcessing) {
        this.transProcessing = transProcessing;
    }

    public BigDecimal getNegotiatedPrem() {
        return negotiatedPrem;
    }

    public void setNegotiatedPrem(BigDecimal negotiatedPrem) {
        this.negotiatedPrem = negotiatedPrem;
    }

    public String getRiskStatus() {
        return riskStatus;
    }

    public void setRiskStatus(String riskStatus) {
        this.riskStatus = riskStatus;
    }

    public String getRiskRefCode() {
        return riskRefCode;
    }

    public void setRiskRefCode(String riskRefCode) {
        this.riskRefCode = riskRefCode;
    }

    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public String getRiskSerialNo() {
        return riskSerialNo;
    }

    public void setRiskSerialNo(String riskSerialNo) {
        this.riskSerialNo = riskSerialNo;
    }

    public String getInsuredPin() {
        return insuredPin;
    }

    public void setInsuredPin(String insuredPin) {
        this.insuredPin = insuredPin;
    }

    public String getRiskShtDesc() {
        return riskShtDesc;
    }

    public void setRiskShtDesc(String riskShtDesc) {
        this.riskShtDesc = riskShtDesc;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public User getRiskUploadedBy() {
        return riskUploadedBy;
    }

    public void setRiskUploadedBy(User riskUploadedBy) {
        this.riskUploadedBy = riskUploadedBy;
    }

    public Date getRiskUploadDate() {
        return riskUploadDate;
    }

    public void setRiskUploadDate(Date riskUploadDate) {
        this.riskUploadDate = riskUploadDate;
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

    public String getInsuredPhoneNo() {
        return insuredPhoneNo;
    }

    public void setInsuredPhoneNo(String insuredPhoneNo) {
        this.insuredPhoneNo = insuredPhoneNo;
    }

    public String getInsuredEmail() {
        return insuredEmail;
    }

    public void setInsuredEmail(String insuredEmail) {
        this.insuredEmail = insuredEmail;
    }

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public String getInsuredType() {
        return insuredType;
    }

    public void setInsuredType(String insuredType) {
        this.insuredType = insuredType;
    }

    public Date getInsuredDOB() {
        return insuredDOB;
    }

    public void setInsuredDOB(Date insuredDOB) {
        this.insuredDOB = insuredDOB;
    }

    public String getInsuredCIF() {
        return insuredCIF;
    }

    public void setInsuredCIF(String insuredCIF) {
        this.insuredCIF = insuredCIF;
    }

    public Date getRiskWef() {
        return riskWef;
    }

    public void setRiskWef(Date riskWef) {
        this.riskWef = riskWef;
    }

    public Date getRiskWet() {
        return riskWet;
    }

    public void setRiskWet(Date riskWet) {
        this.riskWet = riskWet;
    }

    public Integer getRiskTotalInsts() {
        return riskTotalInsts;
    }

    public void setRiskTotalInsts(Integer riskTotalInsts) {
        this.riskTotalInsts = riskTotalInsts;
    }
}

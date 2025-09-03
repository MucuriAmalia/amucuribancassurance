package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.User;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_trans_process_prem_items")
public class TransProcessPremItems extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "trans_prem_risk_Id")
    private Long premRiskId;

    @Column(name = "trans_prem_risk_serial_no", nullable = false)
    private String premSerialNo;

    @Column(name = "trans_prem_risk_sections")
    private String premSections;

    @Column(name = "trans_prem_risk_limit")
    private BigDecimal premLimit;

    @Column(name = "trans_prem_risk_rate")
    private Double premRate;

    @Column(name = "trans_prem_risk_div_factor")
    private Double premDivFactor;

    @ManyToOne
    @JoinColumn(name = "trans_risk_id", nullable = false)
    private TransProcessRisks transProcessRisks;

    @ManyToOne
    @JoinColumn(name = "trans_processing_id", nullable = false)
    private TransactionProcessing transProcessing;

    @Column(name = "trans_prem_risk_status")
    private String premRiskStatus;

    @Column(name = "trans_prem_ref_code")
    private String premRefCode;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "trans_prem_uploaded_by")
    private User premUploadedBy;

    @Column(name = "trans_prem_upload_date")
    private Date premUploadDate;

    public User getPremUploadedBy() {
        return premUploadedBy;
    }

    public void setPremUploadedBy(User premUploadedBy) {
        this.premUploadedBy = premUploadedBy;
    }

    public Date getPremUploadDate() {
        return premUploadDate;
    }

    public void setPremUploadDate(Date premUploadDate) {
        this.premUploadDate = premUploadDate;
    }

    public TransactionProcessing getTransProcessing() {
        return transProcessing;
    }

    public void setTransProcessing(TransactionProcessing transProcessing) {
        this.transProcessing = transProcessing;
    }

    public Long getPremRiskId() {
        return premRiskId;
    }

    public void setPremRiskId(Long premRiskId) {
        this.premRiskId = premRiskId;
    }

    public String getPremSerialNo() {
        return premSerialNo;
    }

    public void setPremSerialNo(String premSerialNo) {
        this.premSerialNo = premSerialNo;
    }

    public String getPremSections() {
        return premSections;
    }

    public void setPremSections(String premSections) {
        this.premSections = premSections;
    }

    public BigDecimal getPremLimit() {
        return premLimit;
    }

    public void setPremLimit(BigDecimal premLimit) {
        this.premLimit = premLimit;
    }

    public Double getPremRate() {
        return premRate;
    }

    public void setPremRate(Double premRate) {
        this.premRate = premRate;
    }

    public Double getPremDivFactor() {
        return premDivFactor;
    }

    public void setPremDivFactor(Double premDivFactor) {
        this.premDivFactor = premDivFactor;
    }

    public TransProcessRisks getTransProcessRisks() {
        return transProcessRisks;
    }

    public void setTransProcessRisks(TransProcessRisks transProcessRisks) {
        this.transProcessRisks = transProcessRisks;
    }

    public String getPremRiskStatus() {
        return premRiskStatus;
    }

    public void setPremRiskStatus(String premRiskStatus) {
        this.premRiskStatus = premRiskStatus;
    }

    public String getPremRefCode() {
        return premRefCode;
    }

    public void setPremRefCode(String premRefCode) {
        this.premRefCode = premRefCode;
    }
}

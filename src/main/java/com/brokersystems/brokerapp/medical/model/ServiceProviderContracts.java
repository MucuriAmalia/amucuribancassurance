package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by peter on 5/12/2017.
 */
@Entity
@Table(name = "sys_brk_provd_contracts")
public class ServiceProviderContracts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spc_id")
    private Long spcId;

    @Column(name = "spc_no",nullable = false)
    private String contractNo;

    @Column(name = "spc_type",nullable = false)
    private String contractType;

    @Column(name="spc_issue_date",nullable = false)
    private Date issueDate;

    @Column(name="spc_wef_date",nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="spc_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @Column(name="spc_status",nullable = false)
    private String status;

    @Column(name="spc_approved")
    private boolean approved;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="spc_provider_id",nullable = false)
    private MedServiceProviders serviceProviders;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="spc_bin_id")
    private BindersDef binder;

    @Column(name="spc_appr_date")
    private Date approvalDate;

    @Column(name="spc_notes",nullable = false)
    private String notes;

    @Transient
    private Long bindCode;

    @Transient
    private String providerName;



    public Long getSpcId() {
        return spcId;
    }

    public void setSpcId(Long spcId) {
        this.spcId = spcId;
    }

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public MedServiceProviders getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(MedServiceProviders serviceProviders) {
        this.serviceProviders = serviceProviders;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}

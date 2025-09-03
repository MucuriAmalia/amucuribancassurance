package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Created by peter on 3/5/2017.
 */
@Entity
@Table(name = "sys_brk_req_docs")
public class RequiredDocs {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "req_id")
    private Long reqId;

    @Column(name = "req_sht_desc",nullable = false,unique = true)
    private String reqShtDesc;

    @Column(name = "req_desc")
    private String reqDesc;

    @Column(name = "req_mandatory")
    private boolean mandatory;

    @Column(name = "req_acc")
    private String appliesAccount;

    @Column(name = "req_sub_agent")
    private String appliesSubAgent;

    @Column(name = "req_client")
    private String appliesClient;

    @Column(name = "req_cop_client")
    private String appliesCorpClient;

    @Column(name = "req_nb")
    private boolean appliesNewBusiness;

    @Column(name = "req_en")
    private boolean appliesEndorsement;

    @Column(name = "req_rn")
    private boolean appliesRenewal;

    @Column(name = "req_cert")
    private boolean appliesCertificate;

    @Column(name = "req_lop")
    private boolean appliesLossOpening;

    @Column(name = "req_clm_pymt")
    private boolean appliesClmPymt;

    @Column(name = "req_prd_type")
    private String periodType;

    @Column(name = "req_period")
    private Long period;

    @Column(name = "req_accrual")
    private String accrualDoc;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="requiredDoc")
    private List<AccountsDocs> accountsDocs;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="requiredDoc")
    private List<ClientDocs> clientDocs;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="requiredDoc")
    private List<SubClassReqdDocs> requiredDocs;

    public Long getReqId() {
        return reqId;
    }

    public void setReqId(Long reqId) {
        this.reqId = reqId;
    }

    public String getReqShtDesc() {
        return reqShtDesc;
    }

    public void setReqShtDesc(String reqShtDesc) {
        this.reqShtDesc = reqShtDesc;
    }

    public String getReqDesc() {
        return reqDesc;
    }

    public void setReqDesc(String reqDesc) {
        this.reqDesc = reqDesc;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isAppliesNewBusiness() {
        return appliesNewBusiness;
    }

    public void setAppliesNewBusiness(boolean appliesNewBusiness) {
        this.appliesNewBusiness = appliesNewBusiness;
    }

    public boolean isAppliesEndorsement() {
        return appliesEndorsement;
    }

    public void setAppliesEndorsement(boolean appliesEndorsement) {
        this.appliesEndorsement = appliesEndorsement;
    }

    public boolean isAppliesRenewal() {
        return appliesRenewal;
    }

    public void setAppliesRenewal(boolean appliesRenewal) {
        this.appliesRenewal = appliesRenewal;
    }

    public boolean isAppliesCertificate() {
        return appliesCertificate;
    }

    public void setAppliesCertificate(boolean appliesCertificate) {
        this.appliesCertificate = appliesCertificate;
    }

    public boolean isAppliesLossOpening() {
        return appliesLossOpening;
    }

    public void setAppliesLossOpening(boolean appliesLossOpening) {
        this.appliesLossOpening = appliesLossOpening;
    }

    public boolean isAppliesClmPymt() {
        return appliesClmPymt;
    }

    public void setAppliesClmPymt(boolean appliesClmPymt) {
        this.appliesClmPymt = appliesClmPymt;
    }

    public String getPeriodType() {
        return periodType;
    }

    public void setPeriodType(String periodType) {
        this.periodType = periodType;
    }

    public Long getPeriod() {
        return period;
    }

    public void setPeriod(Long period) {
        this.period = period;
    }

    public List<SubClassReqdDocs> getRequiredDocs() {
        return requiredDocs;
    }

    public void setRequiredDocs(List<SubClassReqdDocs> requiredDocs) {
        this.requiredDocs = requiredDocs;
    }

    public String getAppliesAccount() {
        return appliesAccount;
    }

    public void setAppliesAccount(String appliesAccount) {
        this.appliesAccount = appliesAccount;
    }

    public String getAppliesClient() {
        return appliesClient;
    }

    public void setAppliesClient(String appliesClient) {
        this.appliesClient = appliesClient;
    }

    public List<AccountsDocs> getAccountsDocs() {
        return accountsDocs;
    }

    public void setAccountsDocs(List<AccountsDocs> accountsDocs) {
        this.accountsDocs = accountsDocs;
    }

    public List<ClientDocs> getClientDocs() {
        return clientDocs;
    }

    public void setClientDocs(List<ClientDocs> clientDocs) {
        this.clientDocs = clientDocs;
    }

    public String getAppliesSubAgent() {
        return appliesSubAgent;
    }

    public void setAppliesSubAgent(String appliesSubAgent) {
        this.appliesSubAgent = appliesSubAgent;
    }

    public String getAppliesCorpClient() {
        return appliesCorpClient;
    }

    public void setAppliesCorpClient(String appliesCorpClient) {
        this.appliesCorpClient = appliesCorpClient;
    }

    public String getAccrualDoc() {
        return accrualDoc;
    }

    public void setAccrualDoc(String accrualDoc) {
        this.accrualDoc = accrualDoc;
    }
}

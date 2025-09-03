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
@Table(name = "sys_brk_trans_timiza")
public class Timiza extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "timiza_id")
    private Long timizaId;

    @Column(name = "timiza_trans_id")
    private String transId;

    @Column(name = "timiza_payment_mode")
    private String paymentMode;

    @Column(name = "timiza_client_fname")
    private String clientFname;

    @Column(name = "timiza_client_othernames")
    private String clientOtherNames;

    @Column(name = "timiza_client_id")
    private String clientId;

    @Column(name = "timiza_client_pin")
    private String clientPin;

    @Column(name = "timiza_client_email")
    private String clientEmail;

    @Column(name = "timiza_client_phone")
    private String clientPhone;

    @Column(name = "timiza_client_cif")
    private String clientCIF;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="timiza_client_type")
    private ClientTypes clientType;
    
    @Column(name = "timiza_trans_date")
    private Date transDate;

    @Column(name = "timiza_pol_term")
    private Integer  polTerm;

    @Column(name = "timiza_product_group")
    private String productGroup;

    @Column(name = "timiza_product_name")
    private String productName;

    @Column(name = "timiza_cover_type")
    private String coverType;

    @Column(name = "timiza_payment_frequency")
    private String frequency;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "timiza_insurer_code")
    private AccountDef insurerCode;

    @Column(name = "timiza_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "timiza_premium")
    private BigDecimal premium;

    @Column(name = "timiza_client_dob")
    private Date clientDOB;

    @Column(name = "timiza_start_date")
    private Date startDate;

    @Column(name = "timiza_end_date")
    private Date endDate;

    @Column(name = "timiza_currency")
    private String currency;

    @Column(name = "timiza_trans_status")
    private String transProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "timiza_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "timiza_processed_by")
    private User processedBy;

    @Column(name = "timiza_uploaded_date")
    private Date uploadedDate;

    @Column(name = "timiza_processed_date")
    private Date processedDate;

    @Column(name = "timiza_pol_type")
    private String timizaPolType;

    @Column(name = "timiza_ref_code")
    private String timizaRefCode;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "timiza_policy_branch")
    private OrgBranch branch;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "timiza_pol_id")
    private PolicyTrans policyTrans;

    public String getPolicyAuthorized() {
        return policyAuthorized;
    }

    public void setPolicyAuthorized(String policyAuthorized) {
        this.policyAuthorized = policyAuthorized;
    }

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    public Long getTimizaId() {
        return timizaId;
    }

    public void setTimizaId(Long timizaId) {
        this.timizaId = timizaId;
    }

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getClientFname() {
        return clientFname;
    }

    public void setClientFname(String clientFname) {
        this.clientFname = clientFname;
    }

    public String getClientOtherNames() {
        return clientOtherNames;
    }

    public void setClientOtherNames(String clientOtherNames) {
        this.clientOtherNames = clientOtherNames;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientPin() {
        return clientPin;
    }

    public void setClientPin(String clientPin) {
        this.clientPin = clientPin;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientCIF() {
        return clientCIF;
    }

    public void setClientCIF(String clientCIF) {
        this.clientCIF = clientCIF;
    }

    public ClientTypes getClientType() {
        return clientType;
    }

    public void setClientType(ClientTypes clientType) {
        this.clientType = clientType;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public String getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(String productGroup) {
        this.productGroup = productGroup;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public BigDecimal getSumInsured() {
        return sumInsured;
    }

    public void setSumInsured(BigDecimal sumInsured) {
        this.sumInsured = sumInsured;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(Date clientDOB) {
        this.clientDOB = clientDOB;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTransProcessed() {
        return transProcessed;
    }

    public void setTransProcessed(String transProcessed) {
        this.transProcessed = transProcessed;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
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

    public String getTimizaPolType() {
        return timizaPolType;
    }

    public void setTimizaPolType(String timizaPolType) {
        this.timizaPolType = timizaPolType;
    }

    public String getTimizaRefCode() {
        return timizaRefCode;
    }

    public void setTimizaRefCode(String timizaRefCode) {
        this.timizaRefCode = timizaRefCode;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public Integer getPolTerm() {return polTerm; }

    public void setPolTerm(Integer polTerm) { this.polTerm = polTerm;}
}

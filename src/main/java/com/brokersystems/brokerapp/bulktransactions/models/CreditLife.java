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
@Table(name = "sys_brk_credit_life")
public class CreditLife extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "credit_life_id")
    private Long creditLifeId;

    @Column(name = "credit_client_fname")
    private String clientFName;

    @Column(name = "credit_client_othernames")
    private String clientOtherNames;

    @Column(name = "credit_client_cif")
    private String clientCIF;

    @Column(name = "credit_client_dob")
    private Date clientDOB;

    @Column(name = "credit_client_pin")
    private String clientPin;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_id")
    private String clientID;

    @Column(name = "credit_cover_type")
    private String coverType;

    @Column(name = "credit_product_group")
    private String productGroup;

    @Column(name = "credit_product_name")
    private String productName;

    @Column(name = "credit_life_term")
    private Integer creditTerm;

    @Column(name = "credit_trans_date")
    private Date transDate;

    @Column(name = "binder_type_id")
    private Long binderTypeId;

    @Column(name = "credit_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "credit_premium_amount")
    private BigDecimal creditPremium;

    @Column(name = "credit_payment_frequency")
    private String frequency;

    @Column(name = "credit_cover_from")
    private Date coverFrom;

    @Column(name = "credit_cover_to")
    private Date coverTo;

    @Column(name = "credit_upload_date")
    private Date uploadedDate;

    @Column(name = "credit_processed_date")
    private Date processedDate;

    @Column(name = "credit_trans_status")
    private String transStatus;

    @Column(name = "credit_trans_code")
    private String transCode;

    @Column(name = "credit_trans_type")
    private String transType;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="credit_client_type")
    private ClientTypes clientType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "credit_insurer_code")
    private AccountDef insurerCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "credit_currecy_code")
    private Currencies currencyCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "credit_processed_by")
    private User processedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "credit_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credit_life_branch")
    private OrgBranch branch;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "credit_life_pol_id")
    private PolicyTrans policyTrans;

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    @Column(name = "loan_id")
    private String loanId;

    @Column(name = "credit_life_contract_name")
    private String contractName;

    public String getPolicyAuthorized() {
        return policyAuthorized;
    }

    public void setPolicyAuthorized(String policyAuthorized) {
        this.policyAuthorized = policyAuthorized;
    }

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    public String getContractName() { return contractName; }
    public void setContractName(String contractName) { this.contractName = contractName; }
    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public Long getCreditLifeId() {
        return creditLifeId;
    }

    public void setCreditLifeId(Long creditLifeId) {
        this.creditLifeId = creditLifeId;
    }

    public String getClientFName() {
        return clientFName;
    }

    public void setClientFName(String clientFName) {
        this.clientFName = clientFName;
    }

    public String getClientOtherNames() {
        return clientOtherNames;
    }

    public void setClientOtherNames(String clientOtherNames) {
        this.clientOtherNames = clientOtherNames;
    }

    public String getClientCIF() {
        return clientCIF;
    }

    public void setClientCIF(String clientCIF) {
        this.clientCIF = clientCIF;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(Date clientDOB) {
        this.clientDOB = clientDOB;
    }

    public String getClientPin() {
        return clientPin;
    }

    public void setClientPin(String clientPin) {
        this.clientPin = clientPin;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
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

    public Integer getCreditTerm() {
        return creditTerm;
    }

    public void setCreditTerm(Integer creditTerm) {
        this.creditTerm = creditTerm;
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

    public BigDecimal getCreditPremium() {
        return creditPremium;
    }

    public void setCreditPremium(BigDecimal creditPremium) {
        this.creditPremium = creditPremium;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Date getCoverFrom() {
        return coverFrom;
    }

    public void setCoverFrom(Date coverFrom) {
        this.coverFrom = coverFrom;
    }

    public Date getCoverTo() {
        return coverTo;
    }

    public void setCoverTo(Date coverTo) {
        this.coverTo = coverTo;
    }

    public Date getUploadedDate() {
        return uploadedDate;
    }

    public void setUploadedDate(Date upoadedDate) {
        this.uploadedDate = upoadedDate;
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

    public ClientTypes getClientType() {
        return clientType;
    }

    public void setClientType(ClientTypes clientType) {
        this.clientType = clientType;
    }

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public Currencies getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currencies currencyCode) {
        this.currencyCode = currencyCode;
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

    public Long getBinderTypeId() {
        return binderTypeId;
    }

    public void setBinderTypeId(Long binderTypeId) {
        this.binderTypeId = binderTypeId;
    }
}

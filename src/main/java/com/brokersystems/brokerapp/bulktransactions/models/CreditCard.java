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
@Table(name = "sys_brk_credit_card")
public class CreditCard extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "card_id")
    private Long cardId;

    @Column(name = "card_client_fname")
    private String clientFName;

    @Column(name = "card_client_othernames")
    private String clientOtherNames;

    @Column(name = "card_client_cif")
    private String clientCIF;

    @Column(name = "card_client_dob")
    private Date clientDOB;

    @Column(name = "card_client_pin")
    private String clientPin;

    @Column(name = "client_phone")
    private String clientPhone;

    @Column(name = "client_email")
    private String clientEmail;

    @Column(name = "client_id")
    private String clientID;

    @Column(name = "card_cover_type")
    private String coverType;

    @Column(name = "card_product_group")
    private String productGroup;

    @Column(name = "card_product_name")
    private String productName;

    @Column(name = "card_trans_date")
    private Date transDate;

    @Column(name = "card_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "card_premium_amount")
    private BigDecimal cardPremium;

    @Column(name = "card_payment_frequency")
    private String frequency;

    @Column(name = "card_cover_from")
    private Date coverFrom;

    @Column(name = "card_cover_to")
    private Date coverTo;

    @Column(name = "card_upload_date")
    private Date uploadedDate;

    @Column(name = "card_processed_date")
    private Date processedDate;

    @Column(name = "card_trans_status")
    private String transStatus;

    @Column(name = "card_trans_code")
    private String transCode;

    @Column(name = "card_trans_type")
    private String transType;

    @Column(name = "card_sales_agent")
    private String salesAgent;

    @Column(name = "card_sales_manager")
    private String salesManager;

    @Column(name = "card_sales_code")
    private Long salesCode;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "card_acct_no")
    private String cardAccount;

    @Column(name = "card_no")
    private String cardNumber;

    @Column(name = "cycles")
    private String cycles;

    @Column(name = "seq_status")
    private String seqStatus;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="card_client_type")
    private ClientTypes clientType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_insurer_code")
    private AccountDef insurerCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_currecy_code")
    private Currencies currencyCode;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "card_processed_by")
    private User processedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "card_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_branch")
    private OrgBranch branch;

    public String getPolicyAuthorized() {
        return policyAuthorized;
    }

    public void setPolicyAuthorized(String policyAuthorized) {
        this.policyAuthorized = policyAuthorized;
    }

    public String getCycles() {
        return cycles;
    }

    public void setCycles(String cycles) {
        this.cycles = cycles;
    }

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "card_pol_id")
    private PolicyTrans policyTrans;

    @Column(name="pol_accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Long getCardId() {
        return cardId;
    }

    public void setCardId(Long cardId) {
        this.cardId = cardId;
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

    public BigDecimal getCardPremium() {
        return cardPremium;
    }

    public void setCardPremium(BigDecimal cardPremium) {
        this.cardPremium = cardPremium;
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

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getSalesAgent() {
        return salesAgent;
    }

    public void setSalesAgent(String salesAgent) {
        this.salesAgent = salesAgent;
    }

    public String getSalesManager() {
        return salesManager;
    }

    public void setSalesManager(String salesManager) {
        this.salesManager = salesManager;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardAccount() {
        return cardAccount;
    }

    public void setCardAccount(String cardAccount) {
        this.cardAccount = cardAccount;
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


    public Date getAccrualInstDate() {
        return accrualInstDate;
    }

    public void setAccrualInstDate(Date accrualInstDate) {
        this.accrualInstDate = accrualInstDate;
    }

    public String getAccrualPaymentType() {
        return accrualPaymentType;
    }

    public void setAccrualPaymentType(String accrualPaymentType) {
        this.accrualPaymentType = accrualPaymentType;
    }
    public String getSeqStatus() { return seqStatus; }

    public void setSeqStatus(String seqStatus) { this.seqStatus = seqStatus; }

    public Long getSalesCode() { return salesCode;}

    public void setSalesCode(Long salesCode) { this.salesCode = salesCode; }
}

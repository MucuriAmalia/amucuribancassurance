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
@Table(name = "sys_brk_trans_wezesha_stock")
public class WezeshaStockCreation extends AuditBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "wezesha_stock_id")
    private Long wezeshaStockId;

    @Column(name = "wezesha_loan_id")
    private String loanId;

    @Column(name = "wezesha_client_fname")
    private String clientFname;

    @Column(name = "wezesha_client_othernames")
    private String clientOtherNames;

    @Column(name = "wezesha_client_id")
    private String clientId;

    @Column(name = "wezesha_client_pin")
    private String clientPin;

    @Column(name = "wezesha_client_email")
    private String clientEmail;

    @Column(name = "wezesha_client_phone")
    private String clientPhone;

    @Column(name = "wezesha_client_cif")
    private String clientCIF;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="wezesha_client_type")
    private ClientTypes clientType;

    @Column(name = "wezesha_business_location")
    private String businessLocation;

    @Column(name = "wezesha_business_type")
    private String businessType;

    @Column(name = "wezesha_trans_date")
    private Date transDate;

    @Column(name = "wezesha_product_group")
    private String productGroup;

    @Column(name = "wezesha_product_name")
    private String productName;

    @Column(name = "wezesha_cover_type")
    private String coverType;

    @Column(name = "wezesha_payment_frequency")
    private String frequency;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "wezesha_insurer_code")
    private AccountDef insurerCode;

    @Column(name = "wezesha_sum_insured")
    private BigDecimal stockSumInsured;

    @Column(name = "wezesha_stock_premium")
    private BigDecimal stockPremium;

    @Column(name = "wezesha_client_dob")
    private Date clientDOB;

    @Column(name = "wezesha_start_date")
    private Date startDate;

    @Column(name = "wezesha_end_date")
    private Date endDate;

    @Column(name = "wezesha_stock_currency")
    private String currency;

    @Column(name = "wezesha_stock_risk_id")
    private String riskId;

    @Column(name = "wezesha_stock_risk_Desc")
    private String riskDesc;

    @Column(name = "wezesha_stock_trans_status")
    private String transProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "wezesha_stock_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "wezesha_stock_processed_by")
    private User processedBy;

    @Column(name = "wezesha_stock_uploaded_date")
    private Date uploadedDate;

    @Column(name = "wezesha_stock_processed_date")
    private Date processedDate;

    @Column(name = "wezesha_stock_pol_type")
    private String wezeshaPolType;

    @Column(name = "wezesha_stock_ref_code")
    private String wezeshaRefCode;

    @Column(name = "wezesha_stock_prem_item")
    private String premiumItem;



    @Column(name = "wezesha_stock_authorized")
    private String authorized;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wezesha_stock_policy_branch")
    private OrgBranch branch;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "wezesha_stock_pol_id")
    private PolicyTrans policyTrans;

    @Column(name="pol_accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;

    public String getPremiumItem() {
        return premiumItem;
    }

    public void setPremiumItem(String premiumItem) {
        this.premiumItem = premiumItem;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public Date getClientDOB() {
        return clientDOB;
    }

    public void setClientDOB(Date clientDOB) {
        this.clientDOB = clientDOB;
    }

    public String getWezeshaPolType() {
        return wezeshaPolType;
    }

    public void setWezeshaPolType(String wezeshaPolType) {
        this.wezeshaPolType = wezeshaPolType;
    }

    public String getWezeshaRefCode() {
        return wezeshaRefCode;
    }

    public void setWezeshaRefCode(String wezeshaRefCode) {
        this.wezeshaRefCode = wezeshaRefCode;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
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

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public Long getWezeshaStockId() {
        return wezeshaStockId;
    }

    public void setWezeshaStockId(Long wezeshaStockId) {
        this.wezeshaStockId = wezeshaStockId;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
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

    public String getBusinessLocation() {
        return businessLocation;
    }

    public void setBusinessLocation(String businessLocation) {
        this.businessLocation = businessLocation;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public BigDecimal getStockSumInsured() {
        return stockSumInsured;
    }

    public void setStockSumInsured(BigDecimal stockSumInsured) {
        this.stockSumInsured = stockSumInsured;
    }

    public BigDecimal getStockPremium() {
        return stockPremium;
    }

    public void setStockPremium(BigDecimal stockPremium) {
        this.stockPremium = stockPremium;
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

    public String getAuthorized() { return authorized; }

    public void setAuthorized(String authorized) { this.authorized = authorized; }

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
}

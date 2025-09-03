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
@Table(name = "sys_brk_create_bulk_policy")
public class BulkPolicyCreation extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "bulk_policy_id")
    private Long bulkPolicyId;

    @Column(name = "bulk_policy_serial_no", nullable = false)
    private String serialNo;

    @Column(name="bulk_policy_product_group", columnDefinition = "TEXT")
    private String productGroup;

    @Column(name = "bulk_policy_pro_name" , columnDefinition = "TEXT")
    private String productName;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_product_def_id")
    private ProductsDef productDefId;

    @Column(name = "bulk_policy_contract_name", columnDefinition = "TEXT")
    private String contractName;

    @Column(name = "bulk_policy_wef")
    private Date coverDateFrom;

    @Column(name="pol_accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;

    @Column(name = "bulk_policy_renew_date")
    private Date renewDate;

    @Column(name = "bulk_policy_currency")
    private String currency;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_currency_id")
    private Currencies currencyId;

    @Column(name = "bulk_policy_client_pin")
    private String clientPin;

    @Column(name = "bulk_policy_sum_insured")
    private BigDecimal sumInsured;

    @Column(name = "bulk_policy_premium")
    private BigDecimal premium;

    @Column(name = "bulk_policy_insured_phone_no")
    private String phoneNumber;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "bulk_policy_underwriter")
    private AccountDef insurerCode;

    @Column(name = "bulk_policy_insured_pin")
    private String insuredPin;

    @Column(name = "bulk_policy_risk_id")
    private String riskId;

    @Column(name = "bulk_policy_risk_desc", columnDefinition = "TEXT")
    private String riskDesc;

    @Column(name = "bulk_policy_cover_type")
    private String coverType;

    @Column(name = "bulk_policy_processed", length = 10)
    private String transProcessed;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_processed_by")
    private User processedBy;

    @Column(name = "bulk_policy_date_uploaded")
    private Date dateUploaded;

    @Column(name = "bulk_policy_date_processed")
    private Date dateProcessed;

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "bulk_policy_pol_id")
    private PolicyTrans policyTrans;


    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_client_def")
    private ClientDef clientDef;

    @Column(name = "bulk_policy_clnt_fname")
    private String clientFname;

    @Column(name = "bulk_policy_clnt_other_names")
    private String clientOtherNames;

    @Column(name = "bulk_policy_clnt_id_no")
    private String clientIdNo;

    @Column(name = "bulk_policy_insured_fname")
    private String insuredFName;

    @Column(name = "bulk_policy_insured_other_names")
    private String insuredOtherNames;

    @Column(name = "bulk_policy_insured_id_no")
    private String insuredIdNo;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_loaded_by")
    private User loadedBy;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_branch")
    private OrgBranch branch;

    @Column(name = "bulk_policy_frequency")
    private String frequency;

    @Column(name="bulk_policy_policy_type")
    private String policyType;

    @Column(name="bulk_policy_business_type")
    private String businessType;

    @Column(name="bulk_policy_ref_code")
    private String refCode;

    @Column(name = "bulk_policy_sales_agent")
    private String salesAgent;


    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_sales_code_id")
    private AccountDef salesAgentId;

    @Column(name = "bulk_policy_sales_code")
    private Long salesCode;

    @Column(name = "binder_type_id")
    private Long binderTypeId;

    // Getter for phoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }
    // Setter for phoneNumber
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    // Getter for sum insured
    public BigDecimal getSumInsured() {return sumInsured;}
    // Setter for sum insured
    public void setSumInsured(BigDecimal sumInsured) { this.sumInsured = sumInsured;}

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public Long getBulkPolicyId() {
        return bulkPolicyId;
    }

    public void setBulkPolicyId(Long bulkPolicyId) {
        this.bulkPolicyId = bulkPolicyId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
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

    public Date getCoverDateFrom() {
        return coverDateFrom;
    }

    public void setCoverDateFrom(Date coverDateFrom) {
        this.coverDateFrom = coverDateFrom;
    }

    public Date getRenewDate() {
        return renewDate;
    }

    public void setRenewDate(Date renewDate) {
        this.renewDate = renewDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getClientPin() {
        return clientPin;
    }

    public void setClientPin(String clientPin) {
        this.clientPin = clientPin;
    }

    public String getInsuredPin() {
        return insuredPin;
    }

    public void setInsuredPin(String insuredPin) {
        this.insuredPin = insuredPin;
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

    public String getCoverType() {
        return coverType;
    }

    public void setCoverType(String coverType) {
        this.coverType = coverType;
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

    public Date getDateUploaded() {
        return dateUploaded;
    }

    public void setDateUploaded(Date dateUploaded) {
        this.dateUploaded = dateUploaded;
    }

    public Date getDateProcessed() {
        return dateProcessed;
    }

    public void setDateProcessed(Date dateProcessed) {
        this.dateProcessed = dateProcessed;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
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

    public String getClientIdNo() {
        return clientIdNo;
    }

    public void setClientIdNo(String clientIdNo) {
        this.clientIdNo = clientIdNo;
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

    public String getInsuredIdNo() {
        return insuredIdNo;
    }

    public void setInsuredIdNo(String insuredIdNo) {
        this.insuredIdNo = insuredIdNo;
    }

    public User getLoadedBy() {
        return loadedBy;
    }

    public void setLoadedBy(User loadedBy) {
        this.loadedBy = loadedBy;
    }

    public OrgBranch getBranch() {
        return branch;
    }

    public void setBranch(OrgBranch branch) {
        this.branch = branch;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getContractName() { return contractName;}

    public void setContractName(String contractName) {this.contractName = contractName;}

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }


    public String getPolicyAuthorized() {
        return policyAuthorized;
    }

    public void setPolicyAuthorized(String policyAuthorized) {
        this.policyAuthorized = policyAuthorized;
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

    public Long getSalesCode() { return salesCode;}

    public void setSalesCode(Long salesCode) { this.salesCode = salesCode; }

    public String getSalesAgent() {
        return salesAgent;
    }

    public void setSalesAgent(String salesAgent) {
        this.salesAgent = salesAgent;
    }

    public Long getBinderTypeId() {
        return binderTypeId;
    }

    public void setBinderTypeId(Long binderTypeId) {
        this.binderTypeId = binderTypeId;
    }

    public ClientDef getClientDef() {
        return clientDef;
    }

    public void setClientDef(ClientDef clientDef) {
        this.clientDef = clientDef;
    }

    public AccountDef getSalesAgentId() {
        return salesAgentId;
    }

    public void setSalesAgentId(AccountDef salesAgentId) {
        this.salesAgentId = salesAgentId;
    }
    public ProductsDef getProductDefId() {
        return productDefId;
    }

    public void setProductDefId(ProductsDef productDefId) {
        this.productDefId = productDefId;
    }
    public Currencies getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Currencies currencyId) {
        this.currencyId = currencyId;
    }

    public SubClassDef getSubClassDef() {
        return subClassDef;
    }

    public void setSubClassDef(SubClassDef subClassDef) {
        this.subClassDef = subClassDef;
    }

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_sub_class_id")
    private SubClassDef subClassDef;

    public CoverTypesDef getCoverTypesDef() {
        return coverTypesDef;
    }

    public void setCoverTypesDef(CoverTypesDef coverTypesDef) {
        this.coverTypesDef = coverTypesDef;
    }

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_cover_type_id")
    private CoverTypesDef coverTypesDef;

    public BindersDef getBindersDefId() {
        return bindersDefId;
    }

    public void setBindersDefId(BindersDef bindersDefId) {
        this.bindersDefId = bindersDefId;
    }

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "bulk_policy_binder_def_id")
    private BindersDef bindersDefId;

}

package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "sys_brk_embed_package_insurance")
public class EmbedPackageInsurance extends AuditBaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "embed_package_id")
    private Long embedPackageId;

    @Column(name = "embed_serial_no")
    private String serialNo;

    @Column(name = "embed_client_name")
    private String clientName;

    @Column(name = "embed_client_other_names")
    private String clientOtherNames;

    @Column(name = "embed_client_dob")
    private Date clientDOB;

    @Column(name = "embed_client_id")
    private String clientId;

    @Column(name = "embed_client_pin")
    private String clientPin;

    @Column(name = "embed_client_email")
    private String clientEmail;

    @Column(name = "embed_client_phone")
    private String clientPhone;

    @ManyToOne
    @JoinColumn(name="embed_client_cou_code")
    private Country countryCode;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="embed_client_clnt_type")
    private ClientTypes tenantType;

    @Column(name = "embed_trans_date")
    private Date transDate;

    @Column(name = "embed_trans_status")
    private String tranStatus;

    @Column(name = "embed_trans_type")
    private String transType;

    @Column(name = "embed_client_cif")
    private String clientCIF;

    @Column(name = "embed_product_group")
    private String productGroup;

    @Column(name = "embed_product_name")
    private String productName;

    @Column(name = "embed_cover_type")
    private String coverType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_currecy_code")
    private Currencies currencyCode;

    @Column(name = "embed_payment_frequency")
    private String frequency;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_insurer_code")
    private AccountDef insurerCode;

    @Column(name = "embed_cover_from")
    private Date coverFrom;

    @Column(name = "embed_processed_date")
    private Date processedDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_processed_by")
    private User processedBy;

    @Column(name = "embed_upload_date")
    private Date uploadDate;

    @Column(name = "embed_cover_to")
    private Date coverTo;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_policy_pol_id")
    private PolicyTrans policyTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "embed_branch_code")
    private OrgBranch orgBranch;

    @Column(name = "embed_ref_code")
    private String refCode;

    public String getPolicyAuthorized() {
        return policyAuthorized;
    }

    public void setPolicyAuthorized(String policyAuthorized) {
        this.policyAuthorized = policyAuthorized;
    }

    @Column(name = "bulk_policy_authorized")
    private String policyAuthorized;

    public String getAccrualPaymentType() {
        return accrualPaymentType;
    }

    public void setAccrualPaymentType(String accrualPaymentType) {
        this.accrualPaymentType = accrualPaymentType;
    }

    @Column(name="pol_accrual_instl_date")
    @Temporal(TemporalType.DATE)
    private Date accrualInstDate;

    @Column(name = "accrual_payment_type")
    private String accrualPaymentType;

    public String getClientCIF() {
        return clientCIF;
    }

    public void setClientCIF(String clientCIF) {
        this.clientCIF = clientCIF;
    }

    public User getProcessedBy() {
        return processedBy;
    }

    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    public String getRefCode() {
        return refCode;
    }

    public void setRefCode(String refCode) {
        this.refCode = refCode;
    }

    public String getTransType() {
        return transType;
    }

    public void setTransType(String transType) {
        this.transType = transType;
    }

    public String getTranStatus() {
        return tranStatus;
    }

    public void setTranStatus(String tranStatus) {
        this.tranStatus = tranStatus;
    }

    public OrgBranch getOrgBranch() {
        return orgBranch;
    }

    public void setOrgBranch(OrgBranch orgBranch) {
        this.orgBranch = orgBranch;
    }

    public Date getProcessedDate() {
        return processedDate;
    }

    public void setProcessedDate(Date processedDate) {
        this.processedDate = processedDate;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public User getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(User uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public AccountDef getInsurerCode() {
        return insurerCode;
    }

    public void setInsurerCode(AccountDef insurerCode) {
        this.insurerCode = insurerCode;
    }

    public PolicyTrans getPolicyTrans() {
        return policyTrans;
    }

    public void setPolicyTrans(PolicyTrans policyTrans) {
        this.policyTrans = policyTrans;
    }

    public Long getEmbedPackageId() {
        return embedPackageId;
    }

    public void setEmbedPackageId(Long embedPackageId) {
        this.embedPackageId = embedPackageId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientOtherNames() {
        return clientOtherNames;
    }

    public void setClientOtherNames(String clientOtherNames) {
        this.clientOtherNames = clientOtherNames;
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

    public Country getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(Country countryCode) {
        this.countryCode = countryCode;
    }

    public ClientTypes getTenantType() {
        return tenantType;
    }

    public void setTenantType(ClientTypes tenantType) {
        this.tenantType = tenantType;
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

    public Currencies getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(Currencies currencyCode) {
        this.currencyCode = currencyCode;
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

    public Date getAccrualInstDate() {
        return accrualInstDate;
    }

    public void setAccrualInstDate(Date accrualInstDate) {
        this.accrualInstDate = accrualInstDate;
    }
}

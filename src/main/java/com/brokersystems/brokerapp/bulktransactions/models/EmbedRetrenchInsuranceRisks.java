package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.ClientTypes;
import com.brokersystems.brokerapp.setup.model.OrgBranch;
import com.brokersystems.brokerapp.setup.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sys_brk_embed_retrench_insurance_risks")
public class EmbedRetrenchInsuranceRisks extends AuditBaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "retrench_embed_id")
    private Long retrenchRetrenchId;

    @Column(name = "retrench_serial_no")
    private String serialNo;

    @Column(name = "retrench_insured_fname")
    private String insuredFname;

    @Column(name = "insured_other_names")
    private String insuredOthernames;

    @Column(name = "retrench_insured_id")
    private String insuredID;

    @Column(name = "retrench_insured_category")
    private String insuredCategory;

    @Column(name = "retrench_trans_code")
    private String transCode;

    @Column(name = "insured_account_no")
    private String insuredAcc;

    @Column(name = "retrench_premium_amt")
    private BigDecimal retrenchPremium;

    @Column(name = "retrench_premium_item")
    private String premItem;

    @Column(name = "retrench_status")
    private String retrenchStatus;

    @Column(name = "retrench_code")
    private String retrenchCode;

    @Column(name = "retrench_isured_dob")
    private Date insuredDOB;

    @Column(name = "retrench_insured_pin")
    private String insuredPin;

    @Column(name = "retrench_insured_phone")
    private String insuredPhone;

    @Column(name = "retrench_isured_email")
    private String insuredEmail;

    @Column(name = "retrench_einsured_cif")
    private String insuredCIF;

    @Column(name = "retrench_month")
    private String retrenchMonth;

    @Column(name = "retrench_acc_open_date")
    private Date accOpenDate;

    @Column(name = "retrench_upload_date")
    private Date uploadDate;

    @Column(name = "retrench_trans_date")
    private Date transDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "retrench_uploaded_by")
    private User uploadedBy;

    @XmlTransient
    @OneToOne
    @JoinColumn(name = "retrench_package_id")
    private EmbedRetrenchInsurance retrenchInsurance;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="retrench_insured_type")
    private ClientTypes tenantType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "retrench_isnured_branch")
    private OrgBranch insuredBranch;

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public Date getTransDate() {
        return transDate;
    }

    public void setTransDate(Date transDate) {
        this.transDate = transDate;
    }

    public Long getRetrenchRetrenchId() {
        return retrenchRetrenchId;
    }

    public void setRetrenchRetrenchId(Long retrenchRetrenchId) {
        this.retrenchRetrenchId = retrenchRetrenchId;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public String getInsuredFname() {
        return insuredFname;
    }

    public void setInsuredFname(String insuredFname) {
        this.insuredFname = insuredFname;
    }

    public String getInsuredOthernames() {
        return insuredOthernames;
    }

    public void setInsuredOthernames(String insuredOthernames) {
        this.insuredOthernames = insuredOthernames;
    }

    public String getInsuredID() {
        return insuredID;
    }

    public void setInsuredID(String insuredID) {
        this.insuredID = insuredID;
    }

    public String getInsuredCategory() {
        return insuredCategory;
    }

    public void setInsuredCategory(String insuredCategory) {
        this.insuredCategory = insuredCategory;
    }

    public String getInsuredAcc() {
        return insuredAcc;
    }

    public void setInsuredAcc(String insuredAcc) {
        this.insuredAcc = insuredAcc;
    }

    public BigDecimal getRetrenchPremium() {
        return retrenchPremium;
    }

    public void setRetrenchPremium(BigDecimal retrenchPremium) {
        this.retrenchPremium = retrenchPremium;
    }

    public String getPremItem() {
        return premItem;
    }

    public void setPremItem(String premItem) {
        this.premItem = premItem;
    }

    public String getRetrenchStatus() {
        return retrenchStatus;
    }

    public void setRetrenchStatus(String retrenchStatus) {
        this.retrenchStatus = retrenchStatus;
    }

    public String getRetrenchCode() {
        return retrenchCode;
    }

    public void setRetrenchCode(String retrenchCode) {
        this.retrenchCode = retrenchCode;
    }

    public Date getInsuredDOB() {
        return insuredDOB;
    }

    public void setInsuredDOB(Date insuredDOB) {
        this.insuredDOB = insuredDOB;
    }

    public String getInsuredPin() {
        return insuredPin;
    }

    public void setInsuredPin(String insuredPin) {
        this.insuredPin = insuredPin;
    }

    public String getInsuredPhone() {
        return insuredPhone;
    }

    public void setInsuredPhone(String insuredPhone) {
        this.insuredPhone = insuredPhone;
    }

    public String getInsuredEmail() {
        return insuredEmail;
    }

    public void setInsuredEmail(String insuredEmail) {
        this.insuredEmail = insuredEmail;
    }

    public String getInsuredCIF() {
        return insuredCIF;
    }

    public void setInsuredCIF(String insuredCIF) {
        this.insuredCIF = insuredCIF;
    }

    public String getRetrenchMonth() {
        return retrenchMonth;
    }

    public void setRetrenchMonth(String retrenchMonth) {
        this.retrenchMonth = retrenchMonth;
    }

    public Date getAccOpenDate() {
        return accOpenDate;
    }

    public void setAccOpenDate(Date accOpenDate) {
        this.accOpenDate = accOpenDate;
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

    public EmbedRetrenchInsurance getRetrenchInsurance() {
        return retrenchInsurance;
    }

    public void setRetrenchInsurance(EmbedRetrenchInsurance retrenchInsurance) {
        this.retrenchInsurance = retrenchInsurance;
    }

    public ClientTypes getTenantType() {
        return tenantType;
    }

    public void setTenantType(ClientTypes tenantType) {
        this.tenantType = tenantType;
    }

    public OrgBranch getInsuredBranch() {
        return insuredBranch;
    }

    public void setInsuredBranch(OrgBranch insuredBranch) {
        this.insuredBranch = insuredBranch;
    }
}

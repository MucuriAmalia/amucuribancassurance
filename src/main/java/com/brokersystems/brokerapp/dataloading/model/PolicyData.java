package com.brokersystems.brokerapp.dataloading.model;


import com.brokersystems.brokerapp.setup.model.User;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="load_policies")
public class PolicyData {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="pk")
    private Long loadId;

    @Column(name="product")
    private String product;

    @Column(name="binder")
    private String binder;

    @Column(name="underwriter")
    private String underwriter;

    @Column(name="branch")
    private String branch;

    @Column(name="currency")
    private String currency;


    @Column(name="effdate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date effdate;

    @Column(name="expdate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date expdate;

    @Column(name="debitdate")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date debitdate;

    @Column(name="risknoteno")
    private String risknoteno;

    @Column(name="negotiatedprem")
    private BigDecimal negotiatedPrem;


    @Column(name="riskid")
    private String riskId;

    @Column(name="riskdescription")
    private String riskDescription;

    @Column(name="covertype")
    private String Covertype;

    @Column(name="benefitvalue")
    private BigDecimal benefitvalue;

    @Column(name="accountnumber")
    private String accountNumber;

    @Column(name="cid")
    private String  cid;

    @Column(name="debitref")
    private String  debitRef;

    @Column(name="loaded")
    private String  loaded;

    @Column(name="accountname")
    private String  accountName;

    @Column(name="amount")
    private BigDecimal amount;

    @Column(name="premium")
    private BigDecimal premium;

    @Column(name="familysize")
    private String  familysize;

    @Column(name="pfno")
    private String  pfno;

    @Column(name="comments")
    private String  comments;

    @Column(name="dateimported")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date dateImported;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="importedby")
    private User importedby;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="polid")
    private PolicyTrans polid;

    public Long getLoadId() {
        return loadId;
    }

    public void setLoadId(Long loadId) {
        this.loadId = loadId;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBinder() {
        return binder;
    }

    public void setBinder(String binder) {
        this.binder = binder;
    }

    public String getUnderwriter() {
        return underwriter;
    }

    public void setUnderwriter(String underwriter) {
        this.underwriter = underwriter;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Date getEffdate() {
        return effdate;
    }

    public void setEffdate(Date effdate) {
        this.effdate = effdate;
    }

    public Date getExpdate() {
        return expdate;
    }

    public void setExpdate(Date expdate) {
        this.expdate = expdate;
    }

    public Date getDebitdate() {
        return debitdate;
    }

    public void setDebitdate(Date debitdate) {
        this.debitdate = debitdate;
    }

    public String getRisknoteno() {
        return risknoteno;
    }

    public void setRisknoteno(String risknoteno) {
        this.risknoteno = risknoteno;
    }

    public BigDecimal getNegotiatedPrem() {
        return negotiatedPrem;
    }

    public void setNegotiatedPrem(BigDecimal negotiatedPrem) {
        this.negotiatedPrem = negotiatedPrem;
    }

    public String getRiskId() {
        return riskId;
    }

    public void setRiskId(String riskId) {
        this.riskId = riskId;
    }

    public String getRiskDescription() {
        return riskDescription;
    }

    public void setRiskDescription(String riskDescription) {
        this.riskDescription = riskDescription;
    }

    public String getCovertype() {
        return Covertype;
    }

    public void setCovertype(String covertype) {
        Covertype = covertype;
    }

    public BigDecimal getBenefitvalue() {
        return benefitvalue;
    }

    public void setBenefitvalue(BigDecimal benefitvalue) {
        this.benefitvalue = benefitvalue;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getDebitRef() {
        return debitRef;
    }

    public void setDebitRef(String debitRef) {
        this.debitRef = debitRef;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getFamilysize() {
        return familysize;
    }

    public void setFamilysize(String familysize) {
        this.familysize = familysize;
    }

    public String getPfno() {
        return pfno;
    }

    public String getLoaded() {
        return loaded;
    }

    public void setLoaded(String loaded) {
        this.loaded = loaded;
    }

    public void setPfno(String pfno) {
        this.pfno = pfno;
    }

    public PolicyTrans getPolid() {
        return polid;
    }

    public void setPolid(PolicyTrans polid) {
        this.polid = polid;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getDateImported() {
        return dateImported;
    }

    public void setDateImported(Date dateImported) {
        this.dateImported = dateImported;
    }

    public User getImportedby() {
        return importedby;
    }

    public void setImportedby(User importedby) {
        this.importedby = importedby;
    }
}

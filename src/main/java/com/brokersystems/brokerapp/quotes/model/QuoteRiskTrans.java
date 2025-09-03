package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.*;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
@Entity
@Table(name="sys_brk_quot_risks")
public class QuoteRiskTrans {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="quot_rsk_id")
    private Long riskId;

    @XmlTransient
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="quot_rsk_pr_id")
    private QuoteProTrans productTrans;

    @Transient
    private Long productCode;

    @Column(name="quote_rsk_sht_desc")
    private String riskShtDesc;

    @Column(name="quot_rsk_desc")
    private String riskDesc;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_rsk_insured_id")
    private ClientDef insured;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_risk_prs_id")
    private ProspectDef prospect;


    @Column(name="quot_rsk_clnt_type")
    private String insuredType;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_rsk_bind_id",nullable=false)
    private BindersDef binder;

    @Column(name="quot_rsk_wef", nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="quot_rsk_wet",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_rsk_sub_id",nullable=false)
    private SubClassDef subclass;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_rsk_cov_id",nullable=false)
    private CoverTypesDef covertype;

    @Column(name="quot_rsk_value")
    private BigDecimal sumInsured;

    @Column(name="quot_rsk_premium")
    private BigDecimal premium;

    @Column(name="quot_rsk_com_rate")
    private BigDecimal commRate;

    @Column(name="quot_rsk_com_amt")
    private BigDecimal commAmt;

    @Column(name="quot_risk_sd")
    private BigDecimal stampDuty;

    @Column(name="quot_risk_phf")
    private BigDecimal phfFund;

    @Column(name="quot_risk_tl")
    private BigDecimal trainingLevy;

    @Column(name="quot_risk_whtx")
    private BigDecimal whtax;

    @Column(name="quot_risk_extras")
    private BigDecimal extras;

    @Column(name="quot_risk_net_prem")
    private BigDecimal netpremium;

    @Column(name="quot_free_limit")
    private BigDecimal freeLimit;

    @Column(name="quot_risk_cal_prem")
    private BigDecimal calcPremium;

    @Column(name="quot_risk_but_charge")
    private BigDecimal butchargePrem;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_rsk_bin_det",nullable=false)
    private BinderDetails binderDetails;

    @Column(name="risk_prorata")
    private String prorata;

    @Transient
    private Long bindCode;

    @Transient
    private Long sclCode;

    @Transient
    private Long coverCode;

    @Transient
    private Long insuredCode;

    @Transient
    private Long binderDet;

    @Transient
    private List<RiskSectionBean> sections;


    public Long getRiskId() {
        return riskId;
    }

    public void setRiskId(Long riskId) {
        this.riskId = riskId;
    }

    public QuoteProTrans getProductTrans() {
        return productTrans;
    }

    public void setProductTrans(QuoteProTrans productTrans) {
        this.productTrans = productTrans;
    }

    public String getRiskShtDesc() {
        return riskShtDesc;
    }

    public void setRiskShtDesc(String riskShtDesc) {
        this.riskShtDesc = riskShtDesc;
    }

    public String getRiskDesc() {
        return riskDesc;
    }

    public void setRiskDesc(String riskDesc) {
        this.riskDesc = riskDesc;
    }

    public ClientDef getInsured() {
        return insured;
    }

    public void setInsured(ClientDef insured) {
        this.insured = insured;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public CoverTypesDef getCovertype() {
        return covertype;
    }

    public void setCovertype(CoverTypesDef covertype) {
        this.covertype = covertype;
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

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public BigDecimal getCommAmt() {
        return commAmt;
    }

    public void setCommAmt(BigDecimal commAmt) {
        this.commAmt = commAmt;
    }

    public BinderDetails getBinderDetails() {
        return binderDetails;
    }

    public void setBinderDetails(BinderDetails binderDetails) {
        this.binderDetails = binderDetails;
    }

    public String getProrata() {
        return prorata;
    }

    public void setProrata(String prorata) {
        this.prorata = prorata;
    }

    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    public BigDecimal getPhfFund() {
        return phfFund;
    }

    public void setPhfFund(BigDecimal phfFund) {
        this.phfFund = phfFund;
    }

    public BigDecimal getTrainingLevy() {
        return trainingLevy;
    }

    public void setTrainingLevy(BigDecimal trainingLevy) {
        this.trainingLevy = trainingLevy;
    }

    public BigDecimal getWhtax() {
        return whtax;
    }

    public void setWhtax(BigDecimal whtax) {
        this.whtax = whtax;
    }

    public BigDecimal getExtras() {
        return extras;
    }

    public void setExtras(BigDecimal extras) {
        this.extras = extras;
    }

    public BigDecimal getNetpremium() {
        return netpremium;
    }

    public void setNetpremium(BigDecimal netpremium) {
        this.netpremium = netpremium;
    }

    public BigDecimal getFreeLimit() {
        return freeLimit;
    }

    public void setFreeLimit(BigDecimal freeLimit) {
        this.freeLimit = freeLimit;
    }

    public BigDecimal getCalcPremium() {
        return calcPremium;
    }

    public void setCalcPremium(BigDecimal calcPremium) {
        this.calcPremium = calcPremium;
    }

    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public Long getSclCode() {
        return sclCode;
    }

    public void setSclCode(Long sclCode) {
        this.sclCode = sclCode;
    }

    public Long getCoverCode() {
        return coverCode;
    }

    public void setCoverCode(Long coverCode) {
        this.coverCode = coverCode;
    }

    public Long getInsuredCode() {
        return insuredCode;
    }

    public void setInsuredCode(Long insuredCode) {
        this.insuredCode = insuredCode;
    }

    public Long getProductCode() {
        return productCode;
    }

    public void setProductCode(Long productCode) {
        this.productCode = productCode;
    }

    public Long getBinderDet() {
        return binderDet;
    }

    public void setBinderDet(Long binderDet) {
        this.binderDet = binderDet;
    }

    public List<RiskSectionBean> getSections() {
        return sections;
    }

    public void setSections(List<RiskSectionBean> sections) {
        this.sections = sections;
    }

    public BigDecimal getButchargePrem() {
        return butchargePrem;
    }

    public void setButchargePrem(BigDecimal butchargePrem) {
        this.butchargePrem = butchargePrem;
    }

    public ProspectDef getProspect() {
        return prospect;
    }

    public void setProspect(ProspectDef prospect) {
        this.prospect = prospect;
    }

    public String getInsuredType() {
        return insuredType;
    }

    public void setInsuredType(String insuredType) {
        this.insuredType = insuredType;
    }
}

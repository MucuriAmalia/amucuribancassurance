package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.ProductsDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import com.brokersystems.brokerapp.uw.model.RiskSectionBean;
import com.brokersystems.brokerapp.uw.model.RiskTransBean;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by peter on 3/12/2017.
 */
@Entity
@Table(name="sys_brk_quot_products")
public class QuoteProTrans {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="quot_pr_id")
    private Long quoteProductId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_pr_pro_id")
    private ProductsDef product;

    @Transient
    private Long prodId;

    @Column(name="quot_pr_wef", nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="quot_pr_wet",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_pr_quot_id")
    private QuoteTrans quoteTrans;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_pr_agent_id")
    private AccountDef agent;

    @Transient
    private Long agentId;

    @Column(name="quot_pr_free_limit")
    private BigDecimal freeLimit;

    @Column(name="quot_pr_cal_premium")
    private BigDecimal calcPremium;

    @Column(name="quot_pr_sum_insured")
    private BigDecimal sumInsured;

    @Column(name="quot_calc_basic_prem")
    private BigDecimal premium;

    @Column(name="quot_pr_sd")
    private BigDecimal stampDuty;

    @Column(name="quot_pr_phf")
    private BigDecimal phfFund;

    @Column(name="quot_pr_tl")
    private BigDecimal trainingLevy;

    @Column(name="quot_pr_whtx")
    private BigDecimal whtax;

    @Column(name="quot_pr_extras")
    private BigDecimal extras;

    @Column(name="quot_pr_comm_rate")
    private BigDecimal commRate;

    @Column(name="quot_pr_comm_amt")
    private BigDecimal commAmt;

    @Column(name="quot_pr_basic_prem")
    private BigDecimal basicPrem;

    @Column(name="quot_pr_net_prem")
    private BigDecimal netPrem;

    @Column(name="quot_pr_converted",length = 1)
    private String converted;

    @OneToOne
    @JoinColumn(name="quot_policy")
    private PolicyTrans convertedReference;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_pr_bind_id")
    private BindersDef binder;

    @Column(name = "quot_pr_selected_comp", length = 1)
    private String selectedProduct;

    @Transient
    private RiskTransBean riskBean;

    @Transient
    private List<RiskSectionBean> sections;

    @Transient
    private Long bindCode;


    public Long getQuoteProductId() {
        return quoteProductId;
    }

    public void setQuoteProductId(Long quoteProductId) {
        this.quoteProductId = quoteProductId;
    }

    public ProductsDef getProduct() {
        return product;
    }

    public void setProduct(ProductsDef product) {
        this.product = product;
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

    public QuoteTrans getQuoteTrans() {
        return quoteTrans;
    }

    public void setQuoteTrans(QuoteTrans quoteTrans) {
        this.quoteTrans = quoteTrans;
    }

    public AccountDef getAgent() {
        return agent;
    }

    public void setAgent(AccountDef agent) {
        this.agent = agent;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
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

    public BigDecimal getBasicPrem() {
        return basicPrem;
    }

    public void setBasicPrem(BigDecimal basicPrem) {
        this.basicPrem = basicPrem;
    }

    public BigDecimal getNetPrem() {
        return netPrem;
    }

    public void setNetPrem(BigDecimal netPrem) {
        this.netPrem = netPrem;
    }


    public Long getBindCode() {
        return bindCode;
    }

    public void setBindCode(Long bindCode) {
        this.bindCode = bindCode;
    }

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public Long getAgentId() {
        return agentId;
    }

    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }

    public String getConverted() {
        return converted;
    }

    public void setConverted(String converted) {
        this.converted = converted;
    }

    public PolicyTrans getConvertedReference() {
        return convertedReference;
    }

    public void setConvertedReference(PolicyTrans convertedReference) {
        this.convertedReference = convertedReference;
    }

    public RiskTransBean getRiskBean() {
        return riskBean;
    }

    public void setRiskBean(RiskTransBean riskBean) {
        this.riskBean = riskBean;
    }

    public List<RiskSectionBean> getSections() {
        return sections;
    }

    public void setSections(List<RiskSectionBean> sections) {
        this.sections = sections;
    }

    public String getSelectedProduct() {
        return selectedProduct;
    }

    public void setSelectedProduct(String selectedProduct) {
        this.selectedProduct = selectedProduct;
    }
}

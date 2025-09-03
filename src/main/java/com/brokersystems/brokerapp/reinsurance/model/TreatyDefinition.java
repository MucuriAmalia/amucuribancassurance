package com.brokersystems.brokerapp.reinsurance.model;

import com.brokersystems.brokerapp.enums.AccountTypeEnum;
import com.brokersystems.brokerapp.reinsurance.enums.TreatyTypes;
import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.User;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="sys_brk_treaty_definition")
public class TreatyDefinition {

    @Id
    @SequenceGenerator(name = "myTreatyDefSeq",sequenceName = "treaty_def_seq",allocationSize=1)
    @GeneratedValue(generator = "myTreatyDefSeq")
    @Column(name="td_id")
    private Long treatyId;
    @Column(name="td_treaty",length = 10)
    @Enumerated(EnumType.STRING)
    private TreatyTypes treatyTypes;
    @Column(name = "td_wef", nullable = false)
    private Date wef;
    @Column(name = "td_wet", nullable = false)
    private Date wet;
    @Column(name = "td_cash_call")
    private BigDecimal cashCall;
    @Column(name = "td_cession_rate")
    private BigDecimal cessionRate;
    @Column(name = "td_cession_rate_type", length = 20)
    private String rateType;
    @Column(name = "td_profit_commission")
    private BigDecimal profitCommission;
    @Column(name = "td_mgmt_fee_rate")
    private BigDecimal managementFeeRate;
    @Column(name = "td_prem_portfolio")
    private BigDecimal premiumPortfolio;
    @Column(name = "td_clm_portfolio")
    private BigDecimal claimsPortfolio;
    @Column(name = "td_limit")
    private BigDecimal limit;
    @Column(name = "td_sum_insured_from")
    private  BigDecimal sumInsuredFrom;
    @Column(name = "td_comm_rate")
    private BigDecimal commRate;
    @Column(name = "td_fac_cede_rate")
    private BigDecimal facCedeRate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="td_cur_code",nullable=false)
    private Currencies currency;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="td_raised_by",nullable=false)
    private User raisedBy;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="td_auth_by",nullable=false)
    private User authorisedBy;
    @Column(name = "td_status",length = 1)
    private String status;

    public BigDecimal getSumInsuredFrom() {
        return sumInsuredFrom;
    }

    public void setSumInsuredFrom(BigDecimal sumInsuredFrom) {
        this.sumInsuredFrom = sumInsuredFrom;
    }

    public Long getTreatyId() {
        return treatyId;
    }

    public void setTreatyId(Long treatyId) {
        this.treatyId = treatyId;
    }

    public TreatyTypes getTreatyTypes() {
        return treatyTypes;
    }

    public void setTreatyTypes(TreatyTypes treatyTypes) {
        this.treatyTypes = treatyTypes;
    }

    public Date getWef() {
        return wef;
    }

    public void setWef(Date wef) {
        this.wef = wef;
    }

    public Date getWet() {
        return wet;
    }

    public void setWet(Date wet) {
        this.wet = wet;
    }

    public BigDecimal getCashCall() {
        return cashCall;
    }

    public void setCashCall(BigDecimal cashCall) {
        this.cashCall = cashCall;
    }

    public BigDecimal getCessionRate() {
        return cessionRate;
    }

    public void setCessionRate(BigDecimal cessionRate) {
        this.cessionRate = cessionRate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getProfitCommission() {
        return profitCommission;
    }

    public void setProfitCommission(BigDecimal profitCommission) {
        this.profitCommission = profitCommission;
    }

    public BigDecimal getManagementFeeRate() {
        return managementFeeRate;
    }

    public void setManagementFeeRate(BigDecimal managementFeeRate) {
        this.managementFeeRate = managementFeeRate;
    }

    public BigDecimal getPremiumPortfolio() {
        return premiumPortfolio;
    }

    public void setPremiumPortfolio(BigDecimal premiumPortfolio) {
        this.premiumPortfolio = premiumPortfolio;
    }

    public BigDecimal getClaimsPortfolio() {
        return claimsPortfolio;
    }

    public void setClaimsPortfolio(BigDecimal claimsPortfolio) {
        this.claimsPortfolio = claimsPortfolio;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public BigDecimal getFacCedeRate() {
        return facCedeRate;
    }

    public void setFacCedeRate(BigDecimal facCedeRate) {
        this.facCedeRate = facCedeRate;
    }

    public Currencies getCurrency() {
        return currency;
    }

    public void setCurrency(Currencies currency) {
        this.currency = currency;
    }

    public User getRaisedBy() {
        return raisedBy;
    }

    public void setRaisedBy(User raisedBy) {
        this.raisedBy = raisedBy;
    }

    public User getAuthorisedBy() {
        return authorisedBy;
    }

    public void setAuthorisedBy(User authorisedBy) {
        this.authorisedBy = authorisedBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

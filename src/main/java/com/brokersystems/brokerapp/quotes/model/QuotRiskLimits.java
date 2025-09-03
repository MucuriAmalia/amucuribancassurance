package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.setup.model.PremRatesDef;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import com.brokersystems.brokerapp.uw.model.RiskTrans;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 3/12/2017.
 */
@Entity
@Table(name="sys_brk_quot_limits")
public class QuotRiskLimits {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="quot_sect_id")
    private Long sectId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_sect_rsk_id")
    private QuoteRiskTrans risk;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_sect_sec_id",nullable=false)
    private SectionsDef section;

    @Column(name="quot_sect_prem")
    private BigDecimal prem;

    @Column(name="quot_sect_calc_prem")
    private BigDecimal calcprem;

    @Column(name="quot_sect_amount")
    private BigDecimal amount;

    @Column(name="quot_sect_rate")
    private BigDecimal rate;

    @Column(name="quot_annual_earnings")
    private BigDecimal annualEarnings;

    @Column(name="quot_sect_div_fact",nullable=false)
    private BigDecimal divFactor;

    @Column(name="quot_sect_free_limit")
    private BigDecimal freeLimit;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="quot_sect_prem_id",nullable=false)
    private PremRatesDef premRates;

    @Column(name="quot_sect_compute")
    private boolean compute;

    @Column(name="quot_sect_multi_rate")
    private BigDecimal multiRate;

    public BigDecimal getAnnualEarnings() {
        return annualEarnings;
    }

    public void setAnnualEarnings(BigDecimal annualEarnings) {
        this.annualEarnings = annualEarnings;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public QuoteRiskTrans getRisk() {
        return risk;
    }

    public void setRisk(QuoteRiskTrans risk) {
        this.risk = risk;
    }

    public SectionsDef getSection() {
        return section;
    }

    public void setSection(SectionsDef section) {
        this.section = section;
    }

    public BigDecimal getPrem() {
        return prem;
    }

    public void setPrem(BigDecimal prem) {
        this.prem = prem;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getDivFactor() {
        return divFactor;
    }

    public void setDivFactor(BigDecimal divFactor) {
        this.divFactor = divFactor;
    }

    public BigDecimal getFreeLimit() {
        return freeLimit;
    }

    public void setFreeLimit(BigDecimal freeLimit) {
        this.freeLimit = freeLimit;
    }

    public PremRatesDef getPremRates() {
        return premRates;
    }

    public void setPremRates(PremRatesDef premRates) {
        this.premRates = premRates;
    }

    public boolean isCompute() {
        return compute;
    }

    public void setCompute(boolean compute) {
        this.compute = compute;
    }

    public BigDecimal getMultiRate() {
        return multiRate;
    }

    public void setMultiRate(BigDecimal multiRate) {
        this.multiRate = multiRate;
    }

    public BigDecimal getCalcprem() {
        return calcprem;
    }

    public void setCalcprem(BigDecimal calcprem) {
        this.calcprem = calcprem;
    }

    @Override
    public String toString() {
        return "QuotRiskLimits{" +
                "sectId=" + sectId +
                ", risk=" + risk +
                ", section=" + section +
                ", prem=" + prem +
                ", calcprem=" + calcprem +
                ", amount=" + amount +
                ", rate=" + rate +
                ", divFactor=" + divFactor +
                ", freeLimit=" + freeLimit +
                ", premRates=" + premRates +
                ", compute=" + compute +
                ", multiRate=" + multiRate +
                '}';
    }
}

package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BindersDef;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 5/4/2017.
 */
@Entity
@Table(name = "sys_brk_bin_loadings")
public class BinderLoadings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bl_id")
    private Long clId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bl_bn_id",nullable = false)
    private BindersDef binder;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bl_al_id",nullable = false)
    private Ailments ailment;

    @Column(name = "bl_ba_desc")
    private String ailmentDesc;

    @Column(name = "bl_rate_type")
    private String rateType;

    @Column(name = "bl_rate")
    private BigDecimal rate;

    @Column(name = "bl_loading")
    private BigDecimal loadingAmt;

    @Column(name = "bl_cost_claim")
    private BigDecimal costPerClaim;

    @Column(name = "bl_upper_limit")
    private BigDecimal upperLimit;

    @Column(name = "bl_waitin_days")
    private Long waitingDays;

    @Column(name = "bl_chronic",length = 1)
    private String chronic;

    public Long getClId() {
        return clId;
    }

    public void setClId(Long clId) {
        this.clId = clId;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public Ailments getAilment() {
        return ailment;
    }

    public void setAilment(Ailments ailment) {
        this.ailment = ailment;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getLoadingAmt() {
        return loadingAmt;
    }

    public void setLoadingAmt(BigDecimal loadingAmt) {
        this.loadingAmt = loadingAmt;
    }

    public String getAilmentDesc() {
        return ailmentDesc;
    }

    public void setAilmentDesc(String ailmentDesc) {
        this.ailmentDesc = ailmentDesc;
    }


    public BigDecimal getCostPerClaim() {
        return costPerClaim;
    }

    public void setCostPerClaim(BigDecimal costPerClaim) {
        this.costPerClaim = costPerClaim;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }

    public Long getWaitingDays() {
        return waitingDays;
    }

    public void setWaitingDays(Long waitingDays) {
        this.waitingDays = waitingDays;
    }

    public String getChronic() {
        return chronic;
    }

    public void setChronic(String chronic) {
        this.chronic = chronic;
    }
}

package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 5/3/2017.
 */
@Entity
@Table(name = "sys_brk_cat_loadings")
public class CategoryLoadings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cl_id")
    private Long clId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cl_cat_id",nullable = false)
    private MedicalCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cl_al_id",nullable = false)
    private Ailments ailment;

    @Column(name = "cl_ba_desc")
    private String ailmentDesc;

    @Column(name = "cl_rate_type")
    private String rateType;

    @Column(name = "cl_rate")
    private BigDecimal rate;

    @Column(name = "cl_loading")
    private BigDecimal loadingAmt;

    @Column(name = "cl_cost_claim")
    private BigDecimal costPerClaim;

    @Column(name = "cl_upper_limit")
    private BigDecimal upperLimit;

    @Column(name = "cl_waitin_days")
    private Long waitingDays;

    @Column(name = "cl_chronic",length = 1)
    private String chronic;

    public Long getClId() {
        return clId;
    }

    public void setClId(Long clId) {
        this.clId = clId;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
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

    public String getAilmentDesc() {
        return ailmentDesc;
    }

    public void setAilmentDesc(String ailmentDesc) {
        this.ailmentDesc = ailmentDesc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryLoadings that = (CategoryLoadings) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        return ailment != null ? ailment.equals(that.ailment) : that.ailment == null;

    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (ailment != null ? ailment.hashCode() : 0);
        return result;
    }
}

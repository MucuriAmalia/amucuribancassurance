package com.brokersystems.brokerapp.medical.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 4/27/2017.
 */
@Entity
@Table(name="sys_brk_cat_benefits")
public class MedCategoryBenefits {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="cb_id")
    private Long sectId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cb_cat_id")
    private MedicalCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cb_cov_id",nullable=false)
    private MedicalCovers cover;

    @Column(name="cb_prem")
    private BigDecimal premium;

    @Column(name="cb_Prev_prem")
    private BigDecimal prevPremium;

    @Column(name="cb_used_prem")
    private BigDecimal usedPremium;

//    @Column(name="cb_unused_prem")
//    private BigDecimal unusedPremium;


    @Column(name="cb_computed_premium")
    private BigDecimal computedPremium;

    @Column(name="cb_unit_premium")
    private BigDecimal unitPremium;

    @Column(name="cb_prev_unit_premium")
    private BigDecimal prevUnitPremium;


    @Column(name="cb_limit")
    private BigDecimal fundLimit;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cb_limit_id")
    private CoverLimits limit;

    @Column(name="cb_wait_prd")
    private Integer waitPeriod;

    @Column(name="cb_applicable_at")
    private String applicableAt;

    @Column(name="cb_status")
    private String status;

    @Column(name="cmb_wef")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="cmb_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    public String getApplicableAt() {
        return applicableAt;
    }

    public void setApplicableAt(String applicableAt) {
        this.applicableAt = applicableAt;
    }

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public MedicalCovers getCover() {
        return cover;
    }

    public void setCover(MedicalCovers cover) {
        this.cover = cover;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public CoverLimits getLimit() {
        return limit;
    }

    public void setLimit(CoverLimits limit) {
        this.limit = limit;
    }

    public BigDecimal getPrevPremium() {
        return prevPremium;
    }

    public void setPrevPremium(BigDecimal prevPremium) {
        this.prevPremium = prevPremium;
    }

    public BigDecimal getFundLimit() {
        return fundLimit;
    }

    public void setFundLimit(BigDecimal fundLimit) {
        this.fundLimit = fundLimit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(Integer waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    public BigDecimal getComputedPremium() {
        return computedPremium;
    }

    public void setComputedPremium(BigDecimal computedPremium) {
        this.computedPremium = computedPremium;
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

    public BigDecimal getUsedPremium() {
        return usedPremium;
    }

    public void setUsedPremium(BigDecimal usedPremium) {
        this.usedPremium = usedPremium;
    }



    public BigDecimal getUnitPremium() {
        return unitPremium;
    }

    public void setUnitPremium(BigDecimal unitPremium) {
        this.unitPremium = unitPremium;
    }

    public BigDecimal getPrevUnitPremium() {
        return prevUnitPremium;
    }

    public void setPrevUnitPremium(BigDecimal precUnitPremium) {
        this.prevUnitPremium = precUnitPremium;
    }

//    public void setUnusedPremium(BigDecimal unusedPremium) {
//        this.unusedPremium = unusedPremium;
//    }
//    public BigDecimal getunusedPremium() {
//        return unusedPremium;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedCategoryBenefits that = (MedCategoryBenefits) o;

        if (!category.equals(that.category)) return false;
        return cover.equals(that.cover);

    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + cover.hashCode();
        return result;
    }
}
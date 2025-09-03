package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.medical.model.CoverLimits;
import com.brokersystems.brokerapp.medical.model.MedicalCategory;
import com.brokersystems.brokerapp.medical.model.MedicalCovers;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by waititu on 09/08/2017.
 */
@Entity
@Table(name="sys_brk_quot_cat_benefits")
public class MedQuotCategoryBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "qcb_id")
    private Long sectId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "qcb_cat_id")
    private MedicalQuoteCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "qcb_cov_id", nullable = false)
    private MedicalCovers cover;

    @Column(name = "qcb_prem")
    private BigDecimal premium;

    @Column(name = "qcb_limit")
    private BigDecimal fundLimit;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "qcb_limit_id")
    private CoverLimits limit;

    @Column(name = "qcb_wait_prd")
    private Integer waitPeriod;

    @Column(name = "qcb_applicable_at")
    private String applicableAt;

    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
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

    public BigDecimal getFundLimit() {
        return fundLimit;
    }

    public void setFundLimit(BigDecimal fundLimit) {
        this.fundLimit = fundLimit;
    }

    public CoverLimits getLimit() {
        return limit;
    }

    public void setLimit(CoverLimits limit) {
        this.limit = limit;
    }

    public Integer getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(Integer waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    public String getApplicableAt() {
        return applicableAt;
    }

    public void setApplicableAt(String applicableAt) {
        this.applicableAt = applicableAt;
    }

    public MedicalQuoteCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalQuoteCategory category) {
        this.category = category;
    }
}
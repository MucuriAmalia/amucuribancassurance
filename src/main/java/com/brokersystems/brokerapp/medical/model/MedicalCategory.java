package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BinderDetails;
import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 4/26/2017.
 */
@Entity
@Table(name = "sys_brk_med_category")
public class MedicalCategory {

    @Id
    @SequenceGenerator(name = "medCategorySeq",sequenceName = "med_category_seq",allocationSize=1)
    @GeneratedValue(generator = "medCategorySeq")
    @Column(name = "mc_id")
    private Long id;

    @Column(name = "mc_sht_desc",nullable = false)
    private String shtDesc;

    @Column(name = "mc_desc",nullable = false)
    private String desc;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_policy_id",nullable = false)
    private PolicyTrans policy;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_bd_det_id",nullable = false)
    private BinderDetails binderDetails;

    @Column(name="mc_calc_premium_amt")
    private BigDecimal premium;

    @Column(name="mc_prevcalc_prem_amt")
    private BigDecimal prevPremium;

    @Column(name="mc_computed_prem_amt")
    private BigDecimal computedPremium;

    @Column(name="mc_loading_factor")
    private BigDecimal loadingFactor;

    @Column(name="mc_prevloading_prem")
    private BigDecimal prevLoadingPrem;

    @Column(name="mc_loading_prem")
    private BigDecimal loadingPrem;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="mc_bed_type_id")
    private BedTypes bedTypes;

    @Column(name="mc_bed_cost")
    private BigDecimal bedCost;

    @Column(name = "mc_flag")
    private String categoryStatus;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public PolicyTrans getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyTrans policy) {
        this.policy = policy;
    }

    public BinderDetails getBinderDetails() {
        return binderDetails;
    }

    public void setBinderDetails(BinderDetails binderDetails) {
        this.binderDetails = binderDetails;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public BigDecimal getLoadingFactor() {
        return loadingFactor;
    }

    public void setLoadingFactor(BigDecimal loadingFactor) {
        this.loadingFactor = loadingFactor;
    }

    public BigDecimal getLoadingPrem() {
        return loadingPrem;
    }

    public void setLoadingPrem(BigDecimal loadingPrem) {
        this.loadingPrem = loadingPrem;
    }

    public BigDecimal getPrevPremium() {
        return prevPremium;
    }

    public void setPrevPremium(BigDecimal prevPremium) {
        this.prevPremium = prevPremium;
    }


    public BigDecimal getComputedPremium() {
        return computedPremium;
    }

    public void setComputedPremium(BigDecimal computedPremium) {
        this.computedPremium = computedPremium;
    }

    public BigDecimal getPrevLoadingPrem() {
        return prevLoadingPrem;
    }

    public void setPrevLoadingPrem(BigDecimal prevLoadingPrem) {
        this.prevLoadingPrem = prevLoadingPrem;
    }

    public String getCategoryStatus() {
        return categoryStatus;
    }

    public void setCategoryStatus(String categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicalCategory category = (MedicalCategory) o;

        return id != null ? id.equals(category.id) : category.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public BedTypes getBedTypes() {
        return bedTypes;
    }

    public void setBedTypes(BedTypes bedTypes) {
        this.bedTypes = bedTypes;
    }

    public BigDecimal getBedCost() {
        return bedCost;
    }

    public void setBedCost(BigDecimal bedCost) {
        this.bedCost = bedCost;
    }
}

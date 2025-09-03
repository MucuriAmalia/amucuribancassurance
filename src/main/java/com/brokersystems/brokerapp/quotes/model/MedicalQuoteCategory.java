package com.brokersystems.brokerapp.quotes.model;

import com.brokersystems.brokerapp.medical.model.BedTypes;
import com.brokersystems.brokerapp.setup.model.BinderDetails;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by waititu on 09/08/2017.
 */
@Entity
@Table(name = "sys_brk_med_quot_category")
public class MedicalQuoteCategory {
        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        @Column(name = "mqc_id")
        private Long id;

        @Column(name = "mqc_sht_desc",nullable = false)
        private String shtDesc;

        @Column(name = "mqc_desc",nullable = false)
        private String desc;

        @XmlTransient
        @ManyToOne
        @JoinColumn(name="mqc_quot_id",nullable = false)
        private QuoteTrans quotation;

        @XmlTransient
        @ManyToOne
        @JoinColumn(name="mqc_bd_det_id",nullable = false)
        private BinderDetails binderDetails;

        @Column(name="mqc_calc_premium_amt")
        private BigDecimal premium;

        @Column(name="mqc_loading_factor")
        private BigDecimal loadingFactor;

        @Column(name="mqc_loading_prem")
        private BigDecimal loadingPrem;

        @XmlTransient
        @ManyToOne
        @JoinColumn(name="mqc_bed_type_id")
        private BedTypes bedTypes;

        @Column(name="mqc_bed_cost")
        private BigDecimal bedCost;

    @Column(name="mqc_principal_age")
    private BigDecimal principalAge;



    @Column(name="mqc_principal_gender")
    private String  principalGender;

    @Column(name="mqc_spouse_age")
    private BigDecimal spouseAge;

    @Column(name="mqc_children_count")
    private BigDecimal childrenCount;

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

    public BigDecimal getPrincipalAge() {
        return principalAge;
    }

    public void setPrincipalAge(BigDecimal principalAge) {
        this.principalAge = principalAge;
    }

    public BigDecimal getSpouseAge() {
        return spouseAge;
    }

    public void setSpouseAge(BigDecimal spouseAge) {
        this.spouseAge = spouseAge;
    }

    public BigDecimal getChildrenCount() {
        return childrenCount;
    }

    public void setChildrenCount(BigDecimal childrenCount) {
        this.childrenCount = childrenCount;
    }

    public QuoteTrans getQuotation() {
        return quotation;
    }

    public void setQuotation(QuoteTrans quotation) {
        this.quotation = quotation;
    }

    public String getPrincipalGender() {
        return principalGender;
    }

    public void setPrincipalGender(String principalGender) {
        this.principalGender = principalGender;
    }
}

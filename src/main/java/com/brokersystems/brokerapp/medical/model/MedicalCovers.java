package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BinderDetails;
import com.brokersystems.brokerapp.setup.model.SectionsDef;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 4/24/2017.
 */
@Entity
@Table(name="sys_brk_med_covers")
public class MedicalCovers {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="med_id")
    private Long id;


    @ManyToOne
    @JoinColumn(name="med_bd_code",nullable=false)
    private BinderDetails binderDet;

    @Column(name="med_min_prem")
    private BigDecimal minPremium;

    @Column(name="med_prorate_full",nullable=false)
    private String proratedFull;

    @ManyToOne
    @JoinColumn(name="med_main_sec_code",nullable=false)
    private SectionsDef mainSection;

    @Column(name="med_main_sec",nullable=false)
    private String mainCover;


    @ManyToOne
    @JoinColumn(name="med_sec_code",nullable=false)
    private SectionsDef section;

    @Column(name="med_limit_amt")
    private BigDecimal limitAmount;

    @Column(name="med_applicable_at")
    private String applicableAt;

    @Column(name="med_dep_gender")
    private boolean dependsOnGender;

    @Column(name="med_gender")
    private String gender;

    @Column(name="med_fund_cov",length = 1)
    private String fundCoverMand;

    @Column(name="med_wait_prd")
    private Integer waitPeriod;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BinderDetails getBinderDet() {
        return binderDet;
    }

    public void setBinderDet(BinderDetails binderDet) {
        this.binderDet = binderDet;
    }

    public BigDecimal getMinPremium() {
        return minPremium;
    }

    public void setMinPremium(BigDecimal minPremium) {
        this.minPremium = minPremium;
    }

    public String getProratedFull() {
        return proratedFull;
    }

    public void setProratedFull(String proratedFull) {
        this.proratedFull = proratedFull;
    }

    public SectionsDef getMainSection() {
        return mainSection;
    }

    public void setMainSection(SectionsDef mainSection) {
        this.mainSection = mainSection;
    }

    public SectionsDef getSection() {
        return section;
    }

    public void setSection(SectionsDef section) {
        this.section = section;
    }

    public String getMainCover() {
        return mainCover;
    }

    public void setMainCover(String mainCover) {
        this.mainCover = mainCover;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public String getApplicableAt() {
        return applicableAt;
    }

    public void setApplicableAt(String applicableAt) {
        this.applicableAt = applicableAt;
    }

    public boolean isDependsOnGender() {
        return dependsOnGender;
    }

    public void setDependsOnGender(boolean dependsOnGender) {
        this.dependsOnGender = dependsOnGender;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFundCoverMand() {
        return fundCoverMand;
    }

    public void setFundCoverMand(String fundCoverMand) {
        this.fundCoverMand = fundCoverMand;
    }

    public Integer getWaitPeriod() {
        return waitPeriod;
    }

    public void setWaitPeriod(Integer waitPeriod) {
        this.waitPeriod = waitPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicalCovers that = (MedicalCovers) o;

        return id.equals(that.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}

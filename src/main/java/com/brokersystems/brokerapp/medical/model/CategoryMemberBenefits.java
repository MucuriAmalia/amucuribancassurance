package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.ClientDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by peter on 4/27/2017.
 */
@Entity
@Table(name="sys_brk_cat_member_benefits")
public class CategoryMemberBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cmb_id")
    private Long cmbId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cmb_cm_id",nullable = false)
    private CategoryMembers member;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cmb_cb_id",nullable = false)
    private MedCategoryBenefits benefit;

    @Column(name="cmb_premium")
    private BigDecimal premium;

    @Column(name="cmb_prev_premium")
    private BigDecimal prevPremium;

    @Column(name="cmb_unused_premium")
    private BigDecimal unusedPremium;

    @Column(name="cmb_computed_premium")
    private BigDecimal computedPremium;

    @Column(name="cmb_wef")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="cmb_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;


    @Column(name="cmb_prev_wef")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date prevwefDate;

    @Column(name="cmb_prev_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date prevwetDate;

    @Column(name="cmb_cmb_id")
    private Long prevcmbId;

    @Column(name="cmb_unit_premium")
    private BigDecimal unitPremium;

    @Column(name="cmb_prev_unit_premium")
    private BigDecimal prevUnitPremium;

    public Long getCmbId() {
        return cmbId;
    }

    public void setCmbId(Long cmbId) {
        this.cmbId = cmbId;
    }

    public CategoryMembers getMember() {
        return member;
    }

    public void setMember(CategoryMembers member) {
        this.member = member;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
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

    public MedCategoryBenefits getBenefit() {
        return benefit;
    }

    public void setBenefit(MedCategoryBenefits benefit) {
        this.benefit = benefit;
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

    public Date getPrevwefDate() {
        return prevwefDate;
    }

    public void setPrevwefDate(Date prevwefDate) {
        this.prevwefDate = prevwefDate;
    }

    public Date getPrevwetDate() {
        return prevwetDate;
    }

    public void setPrevwetDate(Date prevwetDate) {
        this.prevwetDate = prevwetDate;
    }

    public Long getPrevcmbId() {
        return prevcmbId;
    }

    public void setPrevcmbId(Long prevcmbId) {
        this.prevcmbId = prevcmbId;
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

    public void setPrevUnitPremium(BigDecimal prevUnitPremium) {
        this.prevUnitPremium = prevUnitPremium;
    }

    public BigDecimal getUnusedPremium() {
        return unusedPremium;
    }

    public void setUnusedPremium(BigDecimal unusedPremium) {
        this.unusedPremium = unusedPremium;
    }
}
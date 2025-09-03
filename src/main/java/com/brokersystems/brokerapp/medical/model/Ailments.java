package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 5/3/2017.
 */
@Entity
@Table(name = "sys_brk_ailments")
public class Ailments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ba_id")
    private Long baId;

    @Column(name = "ba_sht_desc",nullable = false,unique = true)
    private String baShtDesc;

    @Column(name = "ba_desc",nullable = false)
    private String baDesc;

    @Column(name = "ba_cost_claim")
    private BigDecimal costPerClaim;

    @Column(name = "ba_upper_limit")
    private BigDecimal upperLimit;

    @Column(name = "ba_waitin_days")
    private Long waitingDays;

    @Column(name = "ba_chronic")
    private boolean chronic;

    @Column(name = "ba_gender")
    private String genderAffected;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ba_ail_id")
    private Ailments parentAilment;


    public Long getBaId() {
        return baId;
    }

    public void setBaId(Long baId) {
        this.baId = baId;
    }

    public String getBaShtDesc() {
        return baShtDesc;
    }

    public void setBaShtDesc(String baShtDesc) {
        this.baShtDesc = baShtDesc;
    }

    public String getBaDesc() {
        return baDesc;
    }

    public void setBaDesc(String baDesc) {
        this.baDesc = baDesc;
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

    public boolean isChronic() {
        return chronic;
    }

    public void setChronic(boolean chronic) {
        this.chronic = chronic;
    }

    public String getGenderAffected() {
        return genderAffected;
    }

    public void setGenderAffected(String genderAffected) {
        this.genderAffected = genderAffected;
    }

    public Ailments getParentAilment() {
        return parentAilment;
    }

    public void setParentAilment(Ailments parentAilment) {
        this.parentAilment = parentAilment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ailments ailments = (Ailments) o;

        return baId != null ? baId.equals(ailments.baId) : ailments.baId == null;

    }

    @Override
    public int hashCode() {
        return baId != null ? baId.hashCode() : 0;
    }
}

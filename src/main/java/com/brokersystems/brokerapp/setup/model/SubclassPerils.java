package com.brokersystems.brokerapp.setup.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by peter on 2/27/2017.
 */
@Entity
@Table(name="sys_brk_subclass_perils")
public class SubclassPerils {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sper_id")
    private Long sperId;

    @ManyToOne
    @JoinColumn(name="sper_sub_code",nullable=false)
    private SubClassDef subclass;

    @ManyToOne
    @JoinColumn(name="sper_per_code",nullable=false)
    private PerilsDef peril;

    @Column(name="sper_mandatory")
    private boolean mandatory;

    @Column(name="sper_limit")
    private BigDecimal limit;

    @Column(name="sper_type")
    private String perilType;

    @Column(name="sper_si_limit")
    private String siOrLimit;

    @Column(name="sper_excess_type")
    private String excessType;

    @Column(name="sper_excess")
    private BigDecimal excess;

    @Column(name="sper_excess_min")
    private BigDecimal excessMin;

    @Column(name="sper_excess_max")
    private BigDecimal excessMax;

    @Column(name="sper_expire_clm")
    private boolean expireOnClaim;

    @Column(name="sper_clm_limit")
    private BigDecimal claimLimit;

    @Column(name="sper_person_limit")
    private BigDecimal personLimit;

    @Column(name="sper_desc",length = 500)
    private String description;

    @Column(name="sper_dep_percent")
    private BigDecimal deprecPercent;

    @Column(name="sper_salv_percent")
    private BigDecimal salvagePercent;

    @Column(name="sper_clm_excess_type")
    private String claimExcessType;

    @Column(name="sper_clm_excess")
    private BigDecimal claimExcess;

    @Column(name="sper_clm_excess_min")
    private BigDecimal claimExcessMin;

    @Column(name="sper_clm_excess_max")
    private BigDecimal claimExcessMax;

    @Column(name="sper_depend_loss_type")
    private boolean dependLossType;

    @XmlTransient
    @JsonIgnore
    @OneToMany(mappedBy="subclassPeril")
    private List<BinderSectionPerils> binderSectionPerils;



    public Long getSperId() {
        return sperId;
    }

    public void setSperId(Long sperId) {
        this.sperId = sperId;
    }

    public SubClassDef getSubclass() {
        return subclass;
    }

    public void setSubclass(SubClassDef subclass) {
        this.subclass = subclass;
    }

    public PerilsDef getPeril() {
        return peril;
    }

    public void setPeril(PerilsDef peril) {
        this.peril = peril;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public String getSiOrLimit() {
        return siOrLimit;
    }

    public void setSiOrLimit(String siOrLimit) {
        this.siOrLimit = siOrLimit;
    }

    public String getExcessType() {
        return excessType;
    }

    public void setExcessType(String excessType) {
        this.excessType = excessType;
    }

    public BigDecimal getExcess() {
        return excess;
    }

    public void setExcess(BigDecimal excess) {
        this.excess = excess;
    }

    public BigDecimal getExcessMin() {
        return excessMin;
    }

    public void setExcessMin(BigDecimal excessMin) {
        this.excessMin = excessMin;
    }

    public boolean isExpireOnClaim() {
        return expireOnClaim;
    }

    public void setExpireOnClaim(boolean expireOnClaim) {
        this.expireOnClaim = expireOnClaim;
    }

    public BigDecimal getClaimLimit() {
        return claimLimit;
    }

    public void setClaimLimit(BigDecimal claimLimit) {
        this.claimLimit = claimLimit;
    }

    public BigDecimal getPersonLimit() {
        return personLimit;
    }

    public void setPersonLimit(BigDecimal personLimit) {
        this.personLimit = personLimit;
    }

    public BigDecimal getDeprecPercent() {
        return deprecPercent;
    }

    public void setDeprecPercent(BigDecimal deprecPercent) {
        this.deprecPercent = deprecPercent;
    }

    public BigDecimal getSalvagePercent() {
        return salvagePercent;
    }

    public void setSalvagePercent(BigDecimal salvagePercent) {
        this.salvagePercent = salvagePercent;
    }

    public String getPerilType() {
        return perilType;
    }

    public void setPerilType(String perilType) {
        this.perilType = perilType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getClaimExcessMin() {
        return claimExcessMin;
    }

    public void setClaimExcessMin(BigDecimal claimExcessMin) {
        this.claimExcessMin = claimExcessMin;
    }

    public BigDecimal getClaimExcessMax() {
        return claimExcessMax;
    }

    public void setClaimExcessMax(BigDecimal claimExcessMax) {
        this.claimExcessMax = claimExcessMax;
    }

    public void setDependLossType(boolean dependLossType) {
        this.dependLossType = dependLossType;
    }

    public BigDecimal getExcessMax() {
        return excessMax;
    }

    public void setExcessMax(BigDecimal excessMax) {
        this.excessMax = excessMax;
    }

    public boolean isDependLossType() {
        return dependLossType;
    }

    public String getClaimExcessType() {
        return claimExcessType;
    }

    public void setClaimExcessType(String claimExcessType) {
        this.claimExcessType = claimExcessType;
    }

    public BigDecimal getClaimExcess() {
        return claimExcess;
    }

    public void setClaimExcess(BigDecimal claimExcess) {
        this.claimExcess = claimExcess;
    }

    public List<BinderSectionPerils> getBinderSectionPerils() {
        return binderSectionPerils;
    }

    public void setBinderSectionPerils(List<BinderSectionPerils> binderSectionPerils) {
        this.binderSectionPerils = binderSectionPerils;
    }
}

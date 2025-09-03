package com.brokersystems.brokerapp.reinsurance.model;

import com.brokersystems.brokerapp.setup.model.Currencies;
import com.brokersystems.brokerapp.setup.model.SubClassDef;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_treaty_classes")
public class TreatyClasses {

    @Id
    @SequenceGenerator(name = "myTreatyDefSeq",sequenceName = "treaty_def_seq",allocationSize=1)
    @GeneratedValue(generator = "myTreatyDefSeq")
    @Column(name="tc_id")
    private Long treatyClassId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tc_td_code",nullable=false)
    private TreatyDefinition treatyDefinition;
    @Column(name = "tc_ret_limit")
    private BigDecimal retentionLimit;

    @Column(name = "tc_claim_limit")
    private  BigDecimal claimLimit;
    @Column(name = "tc_insured_limit")
    private  BigDecimal insuredLimit;
    @Column(name = "tc_min_eml")
    private BigDecimal minEml;
    @Column(name = "tc_fa_cede_rate")
    private BigDecimal facCedeRate;

    @Column(name = "tc_ri_prem_tax_rate")
    private BigDecimal riPremTaxRate;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tc_sc_code",nullable=false)
    private SubClassDef subClassDef;

    public BigDecimal getClaimLimit() {
        return claimLimit;
    }

    public void setClaimLimit(BigDecimal claimLimit) {
        this.claimLimit = claimLimit;
    }

    public BigDecimal getInsuredLimit() {
        return insuredLimit;
    }

    public void setInsuredLimit(BigDecimal insuredLimit) {
        this.insuredLimit = insuredLimit;
    }

    public Long getTreatyClassId() {
        return treatyClassId;
    }

    public void setTreatyClassId(Long treatyClassId) {
        this.treatyClassId = treatyClassId;
    }

    public TreatyDefinition getTreatyDefinition() {
        return treatyDefinition;
    }

    public void setTreatyDefinition(TreatyDefinition treatyDefinition) {
        this.treatyDefinition = treatyDefinition;
    }

    public BigDecimal getRetentionLimit() {
        return retentionLimit;
    }

    public void setRetentionLimit(BigDecimal retentionLimit) {
        this.retentionLimit = retentionLimit;
    }

    public BigDecimal getMinEml() {
        return minEml;
    }

    public void setMinEml(BigDecimal minEml) {
        this.minEml = minEml;
    }

    public BigDecimal getFacCedeRate() {
        return facCedeRate;
    }

    public void setFacCedeRate(BigDecimal facCedeRate) {
        this.facCedeRate = facCedeRate;
    }

    public BigDecimal getRiPremTaxRate() {
        return riPremTaxRate;
    }

    public void setRiPremTaxRate(BigDecimal riPremTaxRate) {
        this.riPremTaxRate = riPremTaxRate;
    }

    public SubClassDef getSubClassDef() {
        return subClassDef;
    }

    public void setSubClassDef(SubClassDef subClassDef) {
        this.subClassDef = subClassDef;
    }
}

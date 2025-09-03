package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.ClausesDef;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 5/4/2017.
 */
@Entity
@Table(name = "sys_brk_bin_exclusions")
public class BinderExclusions {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "be_id")
    private Long beId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="be_bin_id",nullable = false)
    private BindersDef binder;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="be_al_id")
    private Ailments ailment;

    @Column(name = "be_ba_desc")
    private String ailmentDesc;

    @Column(name = "be_cost_claim")
    private BigDecimal costPerClaim;

    @Column(name = "be_upper_limit")
    private BigDecimal upperLimit;

    @Column(name = "be_waitin_days")
    private Long waitingDays;

    @Column(name = "be_chronic",length = 1)
    private String chronic;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "be_ben_id")
    private MedicalNetworks medicalnetworks;

    @Column(name = "be_network_desc")
    private String benDesc;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name = "be_clau_id")
    private ClausesDef clausesDef;

    @Column(name="be_clau_wording")
    @Lob
    private String clauWording;

    public Long getBeId() {
        return beId;
    }

    public void setBeId(Long beId) {
        this.beId = beId;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public Ailments getAilment() {
        return ailment;
    }

    public void setAilment(Ailments ailment) {
        this.ailment = ailment;
    }

    public ClausesDef getClausesDef() {
        return clausesDef;
    }

    public void setClausesDef(ClausesDef clausesDef) {
        this.clausesDef = clausesDef;
    }
    public MedicalNetworks getMedicalnetworks() {
        return medicalnetworks;
    }

    public void setMedicalnetworks(MedicalNetworks medicalnetworks) {
        this.medicalnetworks = medicalnetworks;
    }

    public String getAilmentDesc() {
        return ailmentDesc;
    }

    public void setAilmentDesc(String ailmentDesc) {
        this.ailmentDesc = ailmentDesc;
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

    public String getBenDesc() {
        return benDesc;
    }

    public void setBenDesc(String benDesc) {
        this.benDesc = benDesc;
    }

    public String getClauWording() {
        return clauWording;
    }

    public void setClauWording(String clauWording) {
        this.clauWording = clauWording;
    }
}

package com.brokersystems.brokerapp.reinsurance.model;

import com.brokersystems.brokerapp.setup.model.AccountDef;
import com.brokersystems.brokerapp.setup.model.RevenueItemsDef;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_treaty_participants")
public class TreatyParticipants {

    @Id
    @SequenceGenerator(name = "myTreatyDefSeq",sequenceName = "treaty_def_seq",allocationSize=1)
    @GeneratedValue(generator = "myTreatyDefSeq")
    @Column(name="tp_id")
    private Long treatyClassId;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tp_td_code",nullable=false)
    private TreatyDefinition treatyDefinition;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tp_ac_code",nullable=false)
    private AccountDef participant;
    @Column(name = "tp_rein_rate")
    private BigDecimal rate;
    @Column(name = "tp_broker_type", length = 1)
    private String brokerType;

    @Column(name = "tp_tax_chargeable_type", length = 5)
    private String taxChargeable;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tp_br_code")
    private AccountDef broker;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="tp_rev_code")
    private RevenueItemsDef revenueItems;
    @Column(name = "tp_tax_rate")
    private BigDecimal taxRate;
    @Column(name = "tp_tax_rate_type", length = 5)
    private String taxRateType;
    @Column(name = "tp_rec_rate")
    private BigDecimal recoveryPercent;


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

    public AccountDef getParticipant() {
        return participant;
    }

    public void setParticipant(AccountDef participant) {
        this.participant = participant;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getBrokerType() {
        return brokerType;
    }

    public void setBrokerType(String brokerType) {
        this.brokerType = brokerType;
    }

    public String getTaxChargeable() {
        return taxChargeable;
    }

    public void setTaxChargeable(String taxChargeable) {
        this.taxChargeable = taxChargeable;
    }

    public AccountDef getBroker() {
        return broker;
    }

    public void setBroker(AccountDef broker) {
        this.broker = broker;
    }

    public RevenueItemsDef getRevenueItems() {
        return revenueItems;
    }

    public void setRevenueItems(RevenueItemsDef revenueItems) {
        this.revenueItems = revenueItems;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public String getTaxRateType() {
        return taxRateType;
    }

    public void setTaxRateType(String taxRateType) {
        this.taxRateType = taxRateType;
    }

    public BigDecimal getRecoveryPercent() {
        return recoveryPercent;
    }

    public void setRecoveryPercent(BigDecimal recoveryPercent) {
        this.recoveryPercent = recoveryPercent;
    }
}

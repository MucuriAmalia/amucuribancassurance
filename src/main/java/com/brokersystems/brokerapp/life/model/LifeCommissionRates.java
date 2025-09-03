package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 03/12/2017.
 */
@Entity
@Table(name = "sys_brk_life_comm_rates")
public class LifeCommissionRates extends AuditBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comm_id")
    private Long commId;


    @Column(name = "comm_term_from",nullable=false)
    private int commTermFrom;

    @Column(name = "comm_term_to",nullable=false)
    private int commTermTo;

    @Column(name = "comm_yr_from",nullable=false)
    private int commYearFrom;

    @Column(name = "comm_yr_to",nullable=false)
    private int commYearTo;

    @Column(name = "comm_rate",nullable=false)
    private BigDecimal commRate;

    @Column(name = "comm_div_fact",nullable=false)
    private BigDecimal commDivFactor;


    @Column(name="comm_freq_of_pay",nullable=false)
    private String frequency;

    @Column(name="comm_wef",nullable=false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="comm_wet")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    @ManyToOne
    @JoinColumn(name="comm_bd_code",nullable=false)
    private BindersDef binderDef;

    public Long getCommId() {
        return commId;
    }

    public void setCommId(Long commId) {
        this.commId = commId;
    }

    public int getCommTermFrom() {
        return commTermFrom;
    }

    public void setCommTermFrom(int commTermFrom) {
        this.commTermFrom = commTermFrom;
    }

    public int getCommTermTo() {
        return commTermTo;
    }

    public void setCommTermTo(int commTermTo) {
        this.commTermTo = commTermTo;
    }

    public int getCommYearFrom() {
        return commYearFrom;
    }

    public void setCommYearFrom(int commYearFrom) {
        this.commYearFrom = commYearFrom;
    }

    public int getCommYearTo() {
        return commYearTo;
    }

    public void setCommYearTo(int commYearTo) {
        this.commYearTo = commYearTo;
    }

    public BigDecimal getCommRate() {
        return commRate;
    }

    public void setCommRate(BigDecimal commRate) {
        this.commRate = commRate;
    }

    public BigDecimal getCommDivFactor() {
        return commDivFactor;
    }

    public void setCommDivFactor(BigDecimal commDivFactor) {
        this.commDivFactor = commDivFactor;
    }

    public BindersDef getBinderDef() {
        return binderDef;
    }

    public void setBinderDef(BindersDef binderDef) {
        this.binderDef = binderDef;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
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
}

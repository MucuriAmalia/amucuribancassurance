package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 5/9/2017.
 */
@Entity
@Table(name="sys_brk_cover_limits")
public class CoverLimits {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="cl_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="cl_med_code",nullable=false)
    private MedicalCovers covers;

    @Column(name="cl_limit_amt")
    private BigDecimal limitAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MedicalCovers getCovers() {
        return covers;
    }

    public void setCovers(MedicalCovers covers) {
        this.covers = covers;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }


    @Override
    public String toString() {
        return "CoverLimits{" +
                "id=" + id +
                ", limitAmount=" + limitAmount +
                '}';
    }
}

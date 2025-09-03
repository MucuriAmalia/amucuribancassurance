package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 4/21/2017.
 */
@Entity
@Table(name = "sys_brk_spec_fees")
public class SpecialistFees {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spec_id")
    private Long specId;

    @Column(name = "spec_sht_desc",nullable = false,unique = true)
    private String specShtDesc;

    @Column(name = "spec_desc",nullable = false)
    private String specDesc;

    @Column(name = "spec_upper_limit")
    private BigDecimal upperLimit;

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public String getSpecShtDesc() {
        return specShtDesc;
    }

    public void setSpecShtDesc(String specShtDesc) {
        this.specShtDesc = specShtDesc;
    }

    public String getSpecDesc() {
        return specDesc;
    }

    public void setSpecDesc(String specDesc) {
        this.specDesc = specDesc;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }
}

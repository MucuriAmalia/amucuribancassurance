package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 4/21/2017.
 */
@Entity
@Table(name = "sys_brk_lab_tests")
public class LabTest {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "lab_id")
    private Long labId;

    @Column(name = "lab_sht_desc",nullable = false,unique = true)
    private String labShtDesc;

    @Column(name = "lab_desc",nullable = false)
    private String labDesc;

    @Column(name = "lab_upper_limit")
    private BigDecimal upperLimit;

    @Column(name = "lab_cpt_codes")
    private String cptCode;


    public Long getLabId() {
        return labId;
    }

    public void setLabId(Long labId) {
        this.labId = labId;
    }

    public String getLabShtDesc() {
        return labShtDesc;
    }

    public void setLabShtDesc(String labShtDesc) {
        this.labShtDesc = labShtDesc;
    }

    public String getLabDesc() {
        return labDesc;
    }

    public void setLabDesc(String labDesc) {
        this.labDesc = labDesc;
    }

    public BigDecimal getUpperLimit() {
        return upperLimit;
    }

    public void setUpperLimit(BigDecimal upperLimit) {
        this.upperLimit = upperLimit;
    }

    public String getCptCode() {
        return cptCode;
    }

    public void setCptCode(String cptCode) {
        this.cptCode = cptCode;
    }
}

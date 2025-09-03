package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 4/21/2017.
 */

@Entity
@Table(name = "sys_brk_bed_types")
public class BedTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bed_id")
    private Long bedId;

    @Column(name = "bed_sht_desc",nullable = false,unique = true)
    private String bedShtDesc;

    @Column(name = "bed_desc",nullable = false)
    private String bedDesc;

    @Column(name="bed_cost")
    private BigDecimal bedCost;


    public Long getBedId() {
        return bedId;
    }

    public void setBedId(Long bedId) {
        this.bedId = bedId;
    }

    public String getBedShtDesc() {
        return bedShtDesc;
    }

    public void setBedShtDesc(String bedShtDesc) {
        this.bedShtDesc = bedShtDesc;
    }

    public String getBedDesc() {
        return bedDesc;
    }

    public void setBedDesc(String bedDesc) {
        this.bedDesc = bedDesc;
    }

    public BigDecimal getBedCost() {
        return bedCost;
    }

    public void setBedCost(BigDecimal bedCost) {
        this.bedCost = bedCost;
    }
}



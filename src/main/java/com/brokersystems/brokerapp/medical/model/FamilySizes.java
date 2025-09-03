package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by peter on 4/24/2017.
 */
@Entity
@Table(name = "sys_brk_family_size")
public class FamilySizes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "fam_id")
    private Long famId;

    @Column(name = "fam_sht_desc",nullable = false,unique = true)
    private String famShtDesc;

    @Column(name = "fam_desc",nullable = false)
    private String famDesc;

    @Column(name = "fam_size",nullable = false)
    private Long famSize;


    public Long getFamId() {
        return famId;
    }

    public void setFamId(Long famId) {
        this.famId = famId;
    }

    public String getFamShtDesc() {
        return famShtDesc;
    }

    public void setFamShtDesc(String famShtDesc) {
        this.famShtDesc = famShtDesc;
    }

    public String getFamDesc() {
        return famDesc;
    }

    public void setFamDesc(String famDesc) {
        this.famDesc = famDesc;
    }

    public Long getFamSize() {
        return famSize;
    }

    public void setFamSize(Long famSize) {
        this.famSize = famSize;
    }
}

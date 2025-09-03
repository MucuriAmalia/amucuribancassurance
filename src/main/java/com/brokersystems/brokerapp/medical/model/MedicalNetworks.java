package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by peter on 4/21/2017.
 */
@Entity
@Table(name = "sys_brk_networks")
public class MedicalNetworks {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "ben_sht_desc",nullable = false,unique = true)
    private String benShtDesc;

    @Column(name = "ben_desc",nullable = false)
    private String benDesc;

    public Long getBenId() {
        return benId;
    }

    public void setBenId(Long benId) {
        this.benId = benId;
    }

    public String getBenShtDesc() {
        return benShtDesc;
    }

    public void setBenShtDesc(String benShtDesc) {
        this.benShtDesc = benShtDesc;
    }

    public String getBenDesc() {
        return benDesc;
    }

    public void setBenDesc(String benDesc) {
        this.benDesc = benDesc;
    }
}

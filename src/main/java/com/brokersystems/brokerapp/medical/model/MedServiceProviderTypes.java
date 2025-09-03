package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by peter on 4/26/2017.
 */
@Entity
@Table(name="sys_brk_med_prd_types")
public class MedServiceProviderTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spt_id")
    private Long id;

    @Column(name = "spt_sht_desc",nullable = false,unique = true)
    private String shtDesc;

    @Column(name = "spt_desc",nullable = false)
    private String desc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

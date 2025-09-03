package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by peter on 4/21/2017.
 */

@Entity
@Table(name = "sys_brk_dep_types")
public class DependentTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "dep_id")
    private Long depId;

    @Column(name = "dep_sht_desc",nullable = false,unique = true)
    private String depShtDesc;

    @Column(name = "dep_desc",nullable = false)
    private String depDesc;

    @Column(name = "dep_gender")
    private String gender;

    @Column(name = "dep_main_member")
    private boolean mainMember;


    public Long getDepId() {
        return depId;
    }

    public void setDepId(Long depId) {
        this.depId = depId;
    }

    public String getDepShtDesc() {
        return depShtDesc;
    }

    public void setDepShtDesc(String depShtDesc) {
        this.depShtDesc = depShtDesc;
    }

    public String getDepDesc() {
        return depDesc;
    }

    public void setDepDesc(String depDesc) {
        this.depDesc = depDesc;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isMainMember() {
        return mainMember;
    }

    public void setMainMember(boolean mainMember) {
        this.mainMember = mainMember;
    }

}

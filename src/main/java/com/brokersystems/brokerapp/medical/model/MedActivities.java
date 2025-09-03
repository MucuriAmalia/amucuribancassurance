package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.SectionsDef;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 4/21/2017.
 */
@Entity
@Table(name = "sys_brk_med_activities")
public class MedActivities {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ben_id")
    private Long benId;

    @Column(name = "ben_sht_desc",nullable = false,unique = true)
    private String benShtDesc;

    @Column(name = "ben_desc",nullable = false)
    private String benDesc;

    @Column(name = "ben_act_type")
    private String activityType;

    @Column(name = "ben_pre_authored")
    private boolean preauthored;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="ba_ail_id")
    private LabTest services;

    @ManyToOne
    @JoinColumn(name="ba_sec_code")
    private SectionsDef section;

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

    public boolean isPreauthored() {
        return preauthored;
    }

    public void setPreauthored(boolean preauthored) {
        this.preauthored = preauthored;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public LabTest getServices() {
        return services;
    }

    public void setServices(LabTest services) {
        this.services = services;
    }

    public SectionsDef getSection() {
        return section;
    }

    public void setSection(SectionsDef section) {
        this.section = section;
    }
}

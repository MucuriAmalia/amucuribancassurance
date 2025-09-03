package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

/**
 * Created by peter on 3/5/2017.
 */
@Entity
@Table(name="sys_brk_causations")
public class ClmCausations {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="ca_id")
    private Long caId;

    @Column(name="ca_desc",nullable = false)
    private String activityDesc;


    public Long getCaId() {
        return caId;
    }

    public void setCaId(Long caId) {
        this.caId = caId;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

}

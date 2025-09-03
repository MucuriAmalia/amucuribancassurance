package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;

/**
 * Created by peter on 5/9/2017.
 */
@Entity
@Table(name="sys_brk_sub_limits")
public class SubLimits {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="sl_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="sl_cl_code",nullable=false)
    private CoverLimits covLimit;

    @Column(name="sl_sht_desc",nullable = false)
    private String shtDesc;

    @Column(name="sl_desc")
    private String desc;

    @Column(name="sl_limit")
    private String limit;

    @Column(name="sl_wait_prd")
    private Long waitingPeriod;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CoverLimits getCovLimit() {
        return covLimit;
    }

    public void setCovLimit(CoverLimits covLimit) {
        this.covLimit = covLimit;
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

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public Long getWaitingPeriod() {
        return waitingPeriod;
    }

    public void setWaitingPeriod(Long waitingPeriod) {
        this.waitingPeriod = waitingPeriod;
    }
}

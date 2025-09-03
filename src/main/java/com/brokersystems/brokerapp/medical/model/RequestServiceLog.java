package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 6/26/2017.
 */
@Entity
@Table(name = "sys_brk_clm_services_log")
public class RequestServiceLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "csl_id")
    private Long reqServiceLogId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="rs_act_id",nullable = false)
    private MedicalRequestServices requestServices;


    @Column(name = "csl_log_id",nullable = false)
    private String logShtDesc;

    @Column(name = "csl_log_desc",nullable = false,length = 1000)
    private String logDesc;

    public Long getReqServiceLogId() {
        return reqServiceLogId;
    }

    public void setReqServiceLogId(Long reqServiceLogId) {
        this.reqServiceLogId = reqServiceLogId;
    }

    public MedicalRequestServices getRequestServices() {
        return requestServices;
    }

    public void setRequestServices(MedicalRequestServices requestServices) {
        this.requestServices = requestServices;
    }

    public String getLogShtDesc() {
        return logShtDesc;
    }

    public void setLogShtDesc(String logShtDesc) {
        this.logShtDesc = logShtDesc;
    }

    public String getLogDesc() {
        return logDesc;
    }

    public void setLogDesc(String logDesc) {
        this.logDesc = logDesc;
    }
}

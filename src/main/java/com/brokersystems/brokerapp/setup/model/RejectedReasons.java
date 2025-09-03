package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_rejected_reasons")
public class RejectedReasons {

    @Id
    @GeneratedValue
    @Column(name="reason_id")
    private Long reasonId;

    @Column(name="reason_desc",nullable = false)
    private String reasonDesc;

    @Column(name="active_status", length = 1)
    private String activeStatus = "Y"; // Default to active

    public Long getReasonId() {
        return reasonId;
    }

    public void setReasonId(Long reasonId) {
        this.reasonId = reasonId;
    }

    public String getReasonDesc() {
        return reasonDesc;
    }

    public void setReasonDesc(String reasonDesc) {
        this.reasonDesc = reasonDesc;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }


}

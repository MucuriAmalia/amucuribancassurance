package com.brokersystems.brokerapp.setup.model;

import javax.persistence.*;

@Entity
@Table(name="sys_brk_report_groups")
public class ProductReportGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="rpt_id")
    private Long rptId;

    @Column(name="rep_desc")
    private String repDesc;

    @Column(name="rep_sht_desc")
    private String repShtDesc;

    public Long getRptId() {
        return rptId;
    }

    public void setRptId(Long rptId) {
        this.rptId = rptId;
    }

    public String getRepDesc() {
        return repDesc;
    }

    public void setRepDesc(String repDesc) {
        this.repDesc = repDesc;
    }

    public String getRepShtDesc() {
        return repShtDesc;
    }

    public void setRepShtDesc(String repShtDesc) {
        this.repShtDesc = repShtDesc;
    }
}

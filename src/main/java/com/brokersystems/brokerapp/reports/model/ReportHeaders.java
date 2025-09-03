package com.brokersystems.brokerapp.reports.model;

import javax.persistence.*;

/**
 * Created by peter on 2/11/2017.
 */
@Entity
@Table(name = "sys_brk_rpt_headers")
public class ReportHeaders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rh_id")
    private Long rhId;

    @Column(name = "rh_module_code",nullable = false)
    private String moduleCode;

    @Column(name = "rh_module_name",nullable = false)
    private String moduleName;

    public Long getRhId() {
        return rhId;
    }

    public void setRhId(Long rhId) {
        this.rhId = rhId;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
}

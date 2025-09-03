package com.brokersystems.brokerapp.reports.model;

import javax.persistence.*;

/**
 * Created by peter on 2/11/2017.
 */
@Entity
@Table(name = "sys_brk_rpt_params")
public class ReportParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rp_id")
    private Long rpId;

    @ManyToOne
    @JoinColumn(name = "rp_def_code")
    private ReportDefinition reportDef;

    @Column(name = "rp_param_name",nullable = false)
    private String paramName;

    @Column(name = "rp_actual_name",nullable = false)
    private String paramActualName;

    @Column(name = "rp_param_type",nullable = false)
    private String paramType;

    @Column(name = "rp_lov_name")
    private String lovName;

    @Column(name = "rp_options",length = 2000)
    private String options;

    @Column(name = "rp_password_field")
    private String passwordField;

    public Long getRpId() {
        return rpId;
    }

    public void setRpId(Long rpId) {
        this.rpId = rpId;
    }

    public ReportDefinition getReportDef() {
        return reportDef;
    }

    public void setReportDef(ReportDefinition reportDef) {
        this.reportDef = reportDef;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getLovName() {
        return lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    public String getParamActualName() {
        return paramActualName;
    }

    public void setParamActualName(String paramActualName) {
        this.paramActualName = paramActualName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public String getPasswordField() {
        return passwordField;
    }

    public void setPasswordField(String passwordField) {
        this.passwordField = passwordField;
    }
}

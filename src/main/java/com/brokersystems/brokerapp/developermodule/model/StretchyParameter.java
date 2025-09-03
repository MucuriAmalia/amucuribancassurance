package com.brokersystems.brokerapp.developermodule.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "stretchy_parameter")
public class StretchyParameter implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long stpId;

    @Column(name = "parameter_name", nullable = false, unique = true)
    private String paramName;


    @Column(name = "parameter_variable")
    private String parameterVariable;

    @Column(name = "parameter_label", nullable = false)
    private String paramActualName;

    @Column(name = "parameter_display_type")
    private String parameterDisplayType;

    @Column(name = "parameter_format_type", nullable = false)
    private String paramType;

    @Column(name = "parameter_default")
    private String parameterDefault;

    @Column(name = "parameter_sql", columnDefinition = "TEXT")
    private String parameterSql;

    @Column(name = "rp_lov_name")
    private String lovName;

    @Column(name = "rp_options",length = 2000)
    private String options;

    public StretchyParameter() {
    }

    public String getLovName() {
        return lovName;
    }

    public void setLovName(String lovName) {
        this.lovName = lovName;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Long getStpId() {
        return stpId;
    }

    public void setStpId(Long stpId) {
        this.stpId = stpId;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParameterVariable() {
        return parameterVariable;
    }

    public void setParameterVariable(String parameterVariable) {
        this.parameterVariable = parameterVariable;
    }

    public String getParamActualName() {
        return paramActualName;
    }

    public void setParamActualName(String paramActualName) {
        this.paramActualName = paramActualName;
    }

    public String getParameterDisplayType() {
        return parameterDisplayType;
    }

    public void setParameterDisplayType(String parameterDisplayType) {
        this.parameterDisplayType = parameterDisplayType;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getParameterDefault() {
        return parameterDefault;
    }

    public void setParameterDefault(String parameterDefault) {
        this.parameterDefault = parameterDefault;
    }

    public String getParameterSql() {
        return parameterSql;
    }

    public void setParameterSql(String parameterSql) {
        this.parameterSql = parameterSql;
    }

}




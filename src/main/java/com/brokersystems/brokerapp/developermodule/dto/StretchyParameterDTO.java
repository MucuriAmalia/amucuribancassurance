package com.brokersystems.brokerapp.developermodule.dto;

import java.io.Serializable;

public class StretchyParameterDTO implements Serializable {
    private Long stpId;
    private String paramName;
    private String parameterVariable;
    private String paramActualName;
    private String parameterDisplayType;
    private String paramType;
    private String parameterDefault;
    private String parameterSql;
    private String lovName;
    private String options;

    public StretchyParameterDTO() {
    }

    public StretchyParameterDTO(String paramName, String parameterVariable, String paramActualName, String parameterDisplayType,
                                String paramType, String parameterDefault, String parameterSql, String lovName, String options) {
        this.paramName = paramName;
        this.parameterVariable = parameterVariable;
        this.paramActualName = paramActualName;
        this.parameterDisplayType = parameterDisplayType;
        this.paramType = paramType;
        this.parameterDefault = parameterDefault;
        this.parameterSql = parameterSql;
        this.lovName = lovName;
        this.options = options;
    }

    // Getters and setters

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

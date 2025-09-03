package com.brokersystems.brokerapp.developermodule.dto;

import com.brokersystems.brokerapp.users.model.PermissionsDef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportParameterJoinData {

    private Long strId;
    private String strRptName;
    private String reportType;
    private String reportSubtype;
    private String reportCategory;
    private String reportSql;
    private String description;
    private List<String> parameters;
    private PermissionsDef permissionsDef;
    private String strTemplateName;

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
}

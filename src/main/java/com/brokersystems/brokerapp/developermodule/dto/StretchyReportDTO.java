package com.brokersystems.brokerapp.developermodule.dto;

import com.brokersystems.brokerapp.users.model.PermissionsDef;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StretchyReportDTO implements Serializable {
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

}

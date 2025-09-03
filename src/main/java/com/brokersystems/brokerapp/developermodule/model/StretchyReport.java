package com.brokersystems.brokerapp.developermodule.model;

import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.users.model.PermissionsDef;

import javax.persistence.*;

@Entity
@Table(name = "stretchy_report")
public class StretchyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long strId;

    @Column(name = "report_name", unique = true, nullable = false)
    private String strRptName;

    @Column(name = "report_type")
    private String reportType;

    @Column(name = "report_subtype")
    private String reportSubtype;

    @Column(name = "report_category")
    private String reportCategory;

    @Column(name = "report_sql", columnDefinition = "TEXT", nullable = false)
    private String reportSql;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "report_template",nullable = false)
    private String strTemplateName;

    @ManyToOne
    @JoinColumn(name="rpt_permissions")
    private PermissionsDef permissionsDef;

    @Transient
    private String permName;

    public StretchyReport() {
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        UserService userService=null;
        String perms= userService.getPerm(this.permissionsDef.getPermId());
        this.permName=perms;
    }

    public Long getStrId() {
        return strId;
    }

    public void setStrId(Long strId) {
        this.strId = strId;
    }

    public String getStrRptName() {
        return strRptName;
    }

    public void setStrRptName(String strRptName) {
        this.strRptName = strRptName;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportSubtype() {
        return reportSubtype;
    }

    public void setReportSubtype(String reportSubtype) {
        this.reportSubtype = reportSubtype;
    }

    public String getReportCategory() {
        return reportCategory;
    }

    public void setReportCategory(String reportCategory) {
        this.reportCategory = reportCategory;
    }

    public String getReportSql() {
        return reportSql;
    }

    public void setReportSql(String reportSql) {
        this.reportSql = reportSql;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStrTemplateName() {
        return strTemplateName;
    }

    public void setStrTemplateName(String strTemplateName) {
        this.strTemplateName = strTemplateName;
    }

    public PermissionsDef getPermissionsDef() {
        return permissionsDef;
    }

    public void setPermissionsDef(PermissionsDef permissionsDef) {
        this.permissionsDef = permissionsDef;
    }
}




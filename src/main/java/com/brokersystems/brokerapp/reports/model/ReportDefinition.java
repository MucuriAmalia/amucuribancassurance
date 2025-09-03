package com.brokersystems.brokerapp.reports.model;

import com.brokersystems.brokerapp.setup.model.AccountTypes;
import com.brokersystems.brokerapp.setup.service.UserService;
import com.brokersystems.brokerapp.setup.service.impl.UserServiceImpl;
import com.brokersystems.brokerapp.users.model.PermissionsDef;
import com.brokersystems.brokerapp.users.repository.PermissionsRepo;

import javax.persistence.*;

/**
 * Created by peter on 2/11/2017.
 */
@Entity
@Table(name = "sys_brk_rpt_def")
public class ReportDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rpt_id")
    private Long rptId;

    @ManyToOne
    @JoinColumn(name = "rpt_header_code")
    private ReportHeaders reportHeader;

    @Column(name = "rpt_name",nullable = false)
    private String rptName;

    @Column(name = "rpt_desc",nullable = false)
    private String rptDescription;

    @Column(name = "rpt_status",nullable = false)
    private boolean active;

    @Column(name = "rpt_template",nullable = false)
    private String rptTemplateName;

    @Column(name = "rpt_password_protect")
    private String passwordProtect;

    @ManyToOne
    @JoinColumn(name="rpt_permissions")
    private PermissionsDef permissionsDef;

    @Transient
    private String permName;

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        UserService userService=null;
        String perms= userService.getPerm(this.permissionsDef.getPermId());
        this.permName=perms;
    }

    public PermissionsDef getPermissionsDef() {
        return permissionsDef;
    }

    public void setPermissionsDef(PermissionsDef permissionsDef) {
        this.permissionsDef = permissionsDef;
    }

    public Long getRptId() {
        return rptId;
    }

    public void setRptId(Long rptId) {
        this.rptId = rptId;
    }

    public ReportHeaders getReportHeader() {
        return reportHeader;
    }

    public void setReportHeader(ReportHeaders reportHeader) {
        this.reportHeader = reportHeader;
    }

    public String getRptName() {
        return rptName;
    }

    public void setRptName(String rptName) {
        this.rptName = rptName;
    }

    public String getRptDescription() {
        return rptDescription;
    }

    public void setRptDescription(String rptDescription) {
        this.rptDescription = rptDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRptTemplateName() {
        return rptTemplateName;
    }

    public void setRptTemplateName(String rptTemplateName) {
        this.rptTemplateName = rptTemplateName;
    }

    public String getPasswordProtect() {
        return passwordProtect;
    }

    public void setPasswordProtect(String passwordProtect) {
        this.passwordProtect = passwordProtect;
    }
}

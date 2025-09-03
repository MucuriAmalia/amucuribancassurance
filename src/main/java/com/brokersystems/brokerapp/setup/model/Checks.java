package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.users.model.PermissionsDef;

import javax.persistence.*;

/**
 * Created by HP on 10/4/2017.
 */
@Entity
@Table(name="sys_brk_checks")
public class Checks {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="check_id")
    private Long checkId;

    @Column(name="check_sht_desc",nullable=false,unique=true)
    private String checkShtDesc;

    @Column(name="check_name",nullable=false)
    private String checkName;

    @ManyToOne
    @JoinColumn(name="check_perm_id")
    private PermissionsDef permissionsDef;

    @Column(name="check_type",nullable=false)
    private String checkType;

    @Column(name="check_status",nullable=false)
    private boolean status;

    @Column(name="check_appli",length = 1)
    private String checkApplicable;

    @Column(name="check_common",length = 1)
    private String checkCommon;

    @Transient
    private Long prodCheckId;


    public Long getCheckId() {
        return checkId;
    }

    public void setCheckId(Long checkId) {
        this.checkId = checkId;
    }

    public String getCheckShtDesc() {
        return checkShtDesc;
    }

    public void setCheckShtDesc(String checkShtDesc) {
        this.checkShtDesc = checkShtDesc;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public PermissionsDef getPermissionsDef() {
        return permissionsDef;
    }

    public void setPermissionsDef(PermissionsDef permissionsDef) {
        this.permissionsDef = permissionsDef;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getCheckApplicable() {
        return checkApplicable;
    }

    public void setCheckApplicable(String checkApplicable) {
        this.checkApplicable = checkApplicable;
    }

    public String getCheckCommon() {
        return checkCommon;
    }

    public void setCheckCommon(String checkCommon) {
        this.checkCommon = checkCommon;
    }

    public Long getProdCheckId() {
        return prodCheckId;
    }

    public void setProdCheckId(Long prodCheckId) {
        this.prodCheckId = prodCheckId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Checks checks = (Checks) o;

        return checkId != null ? checkId.equals(checks.checkId) : checks.checkId == null;

    }

    @Override
    public int hashCode() {
        return checkId != null ? checkId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Checks{" +
                "checkId=" + checkId +
                ", checkShtDesc='" + checkShtDesc + '\'' +
                ", checkName='" + checkName + '\'' +
                '}';
    }
}

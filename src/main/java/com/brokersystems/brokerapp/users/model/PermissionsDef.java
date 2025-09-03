package com.brokersystems.brokerapp.users.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.Module;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;

/**
 * Created by peter on 4/15/2017.
 */
@Entity
//@Cacheable(true)
@Table(name ="sys_brk_permissions")
public class PermissionsDef {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="perm_id")
    private Long permId;

    @Column(name="perm_name")
    private String permName;

    @Column(name="perm_desc")
    private String permDesc;

    @Column(name="perm_acc_type")
    private String accessType;

    @XmlTransient
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="perm_module_id")
    private ModulesDef module;

    @Column(name = "perm_enable_maker_checker",length = 1)
    private String enableMakerChecker;

    @Transient
    private Long roleId;

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getEnableMakerChecker() {
        return enableMakerChecker;
    }

    public void setEnableMakerChecker(String enableMakerChecker) {
        this.enableMakerChecker = enableMakerChecker;
    }

    public Long getPermId() {
        return permId;
    }

    public void setPermId(Long permId) {
        this.permId = permId;
    }

    public String getPermName() {
        return permName;
    }

    public void setPermName(String permName) {
        this.permName = permName;
    }

    public String getPermDesc() {
        return permDesc;
    }

    public void setPermDesc(String permDesc) {
        this.permDesc = permDesc;
    }

    public String getAccessType() {
        return accessType;
    }

    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    public ModulesDef getModule() {
        return module;
    }

    public void setModule(ModulesDef module) {
        this.module = module;
    }
}

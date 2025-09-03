package com.brokersystems.brokerapp.users.model;

import javax.persistence.*;

/**
 * Created by peter on 4/17/2017.
 */
@Entity
@Table(name ="sys_brk_sys_modules")
public class ModulesDef {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="module_id")
    private Long moduleId;

    @Column(name="module_sht_desc",nullable = false)
    private String shtDesc;

    @Column(name="module_name",nullable = false)
    private String moduleName;

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }
}

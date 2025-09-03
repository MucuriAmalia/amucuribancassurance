package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BinderDetails;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.brokersystems.brokerapp.setup.model.Checks;
import com.brokersystems.brokerapp.setup.model.ProductChecks;

import javax.persistence.*;

/**
 * Created by peter on 4/25/2017.
 */
@Entity
@Table(name="sys_brk_med_bin_rules")
public class MedicalBinderRules {

    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="rule_id")
    private Long ruleId;

    @Column(name="rule_sht_desc")
    private String shtDesc;

    @Column(name="rule_desc",nullable = false,length = 2000)
    private String desc;

    @Column(name="rule_value",nullable = false)
    private String value;

    @Column(name = "rule_mandatory",length = 1)
    private String mandatory;

    @ManyToOne
    @JoinColumn(name="rule_bd_code",nullable=false)
    private BindersDef binder;

    @ManyToOne
    @JoinColumn(name="rule_check_code")
    private ProductChecks checks;

    public Long getRuleId() {
        return ruleId;
    }

    public void setRuleId(Long ruleId) {
        this.ruleId = ruleId;
    }

    public String getShtDesc() {
        return shtDesc;
    }

    public void setShtDesc(String shtDesc) {
        this.shtDesc = shtDesc;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    public ProductChecks getChecks() {
        return checks;
    }

    public void setChecks(ProductChecks checks) {
        this.checks = checks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MedicalBinderRules that = (MedicalBinderRules) o;

        return ruleId.equals(that.ruleId);

    }

    @Override
    public int hashCode() {
        return ruleId.hashCode();
    }
}

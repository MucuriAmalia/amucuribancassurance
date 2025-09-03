package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 4/28/2017.
 */
@Entity
@Table(name="sys_brk_cat_rules")
public class CategoryRules {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="cr_id")
    private Long sectId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cr_cat_id",nullable = false)
    private MedicalCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cr_rule_id",nullable = false)
    private MedicalBinderRules binderRules;

    @Column(name="cr_sht_desc",nullable = false)
    private String shtDesc;

    @Column(name="cr_desc",nullable = false,length = 2000)
    private String desc;

    @Column(name="cr_value",nullable = false)
    private String value;


    public Long getSectId() {
        return sectId;
    }

    public void setSectId(Long sectId) {
        this.sectId = sectId;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public MedicalBinderRules getBinderRules() {
        return binderRules;
    }

    public void setBinderRules(MedicalBinderRules binderRules) {
        this.binderRules = binderRules;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryRules that = (CategoryRules) o;

        if (!category.equals(that.category)) return false;
        return binderRules.equals(that.binderRules);

    }

    @Override
    public int hashCode() {
        int result = category.hashCode();
        result = 31 * result + binderRules.hashCode();
        return result;
    }
}

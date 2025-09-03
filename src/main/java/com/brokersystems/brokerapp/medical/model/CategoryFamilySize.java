package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 4/27/2017.
 */
@Entity
@Table(name = "sys_brk_scheme_dep_types")
public class CategoryFamilySize {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "fs_id")
    private Long fsId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="fs_dp_id",nullable = false)
    private DependentTypes dependentTypes;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="fs_cat_id",nullable = false)
    private MedicalCategory category;

    @Column(name = "fs_max_no")
    private Long maxNumber;

    @Column(name = "fs_min_age")
    private Long minAge;

    @Column(name = "fs_max_age")
    private Long maxAge;

    public Long getFsId() {
        return fsId;
    }

    public void setFsId(Long fsId) {
        this.fsId = fsId;
    }

    public DependentTypes getDependentTypes() {
        return dependentTypes;
    }

    public void setDependentTypes(DependentTypes dependentTypes) {
        this.dependentTypes = dependentTypes;
    }

    public Long getMaxNumber() {
        return maxNumber;
    }

    public void setMaxNumber(Long maxNumber) {
        this.maxNumber = maxNumber;
    }

    public Long getMinAge() {
        return minAge;
    }

    public void setMinAge(Long minAge) {
        this.minAge = minAge;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }
}

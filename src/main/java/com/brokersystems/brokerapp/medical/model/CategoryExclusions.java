package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 5/3/2017.
 */
@Entity
@Table(name = "sys_brk_cat_exclusions")
public class CategoryExclusions {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cl_id")
    private Long clId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cl_cat_id",nullable = false)
    private MedicalCategory category;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cl_al_id",nullable = false)
    private Ailments ailment;

    public Long getClId() {
        return clId;
    }

    public void setClId(Long clId) {
        this.clId = clId;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    public Ailments getAilment() {
        return ailment;
    }

    public void setAilment(Ailments ailment) {
        this.ailment = ailment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CategoryExclusions that = (CategoryExclusions) o;

        if (category != null ? !category.equals(that.category) : that.category != null) return false;
        return ailment != null ? ailment.equals(that.ailment) : that.ailment == null;

    }

    @Override
    public int hashCode() {
        int result = category != null ? category.hashCode() : 0;
        result = 31 * result + (ailment != null ? ailment.hashCode() : 0);
        return result;
    }
}

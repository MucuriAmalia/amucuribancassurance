package com.brokersystems.brokerapp.medical.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 5/5/2017.
 */
@Entity
@Table(name = "sys_brk_cat_providers")
public class CatalogueProviders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cp_id")
    private Long cpId;

    @ManyToOne
    @JoinColumn(name="cp_provider_code",nullable=false)
    private MedServiceProviders providers;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="cp_cat_id",nullable = false)
    private MedicalCategory category;

    public Long getCpId() {
        return cpId;
    }

    public void setCpId(Long cpId) {
        this.cpId = cpId;
    }

    public MedServiceProviders getProviders() {
        return providers;
    }

    public void setProviders(MedServiceProviders providers) {
        this.providers = providers;
    }

    public MedicalCategory getCategory() {
        return category;
    }

    public void setCategory(MedicalCategory category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CatalogueProviders that = (CatalogueProviders) o;

        if (providers != null ? !providers.equals(that.providers) : that.providers != null) return false;
        return category != null ? category.equals(that.category) : that.category == null;

    }

    @Override
    public int hashCode() {
        int result = providers != null ? providers.hashCode() : 0;
        result = 31 * result + (category != null ? category.hashCode() : 0);
        return result;
    }
}

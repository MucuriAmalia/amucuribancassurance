package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BindersDef;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by peter on 5/5/2017.
 */
@Entity
@Table(name = "sys_brk_bin_providers")
public class BinderProviders {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bp_id")
    private Long beId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bp_bin_id",nullable = false)
    private BindersDef binder;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="bp_al_id",nullable = false)
    private MedServiceProviders provider;

    public Long getBeId() {
        return beId;
    }

    public void setBeId(Long beId) {
        this.beId = beId;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public MedServiceProviders getProvider() {
        return provider;
    }

    public void setProvider(MedServiceProviders provider) {
        this.provider = provider;
    }
}

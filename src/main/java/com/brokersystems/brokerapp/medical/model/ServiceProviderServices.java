package com.brokersystems.brokerapp.medical.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * Created by waititu on 17/10/2017.
 */
@Entity
@Table(name = "sys_brk_provd_services")
public class ServiceProviderServices {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "sps_id")
    private Long spsId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="sps_provider_id",nullable = false)
    private MedServiceProviders serviceProviders;


    @XmlTransient
    @ManyToOne
    @JoinColumn(name="sps_lab_id",nullable = false)
    private LabTest medicalServices;

    @Column(name="sps_wef_date",nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="sps_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    public Long getSpsId() {
        return spsId;
    }

    public void setSpsId(Long spsId) {
        this.spsId = spsId;
    }

    public MedServiceProviders getServiceProviders() {
        return serviceProviders;
    }

    public void setServiceProviders(MedServiceProviders serviceProviders) {
        this.serviceProviders = serviceProviders;
    }



    public Date getWefDate() {
        return wefDate;
    }

    public void setWefDate(Date wefDate) {
        this.wefDate = wefDate;
    }

    public Date getWetDate() {
        return wetDate;
    }

    public void setWetDate(Date wetDate) {
        this.wetDate = wetDate;
    }

    public LabTest getMedicalServices() {
        return medicalServices;
    }

    public void setMedicalServices(LabTest medicalServices) {
        this.medicalServices = medicalServices;
    }
}

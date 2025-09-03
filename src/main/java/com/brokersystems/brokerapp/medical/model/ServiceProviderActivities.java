package com.brokersystems.brokerapp.medical.model;

/**
 * Created by waititu on 02/11/2017.
 */
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 02/11/2017.
 */
@Entity
@Table(name = "sys_brk_provider_activities")
public class ServiceProviderActivities {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "spa_id")
    private Long spaId;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="spa_sps_id")
    private ServiceProviderServices services;

    @ManyToOne
    @JoinColumn(name="spa_act_id")
    private MedActivities activities;

    @Column(name = "spa_fee")
    private BigDecimal activityFee;


    @Column(name="spa_wef_date",nullable = false)
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="spa_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;

    public BigDecimal getActivityFee() {
        return activityFee;
    }

    public void setActivityFee(BigDecimal activityFee) {
        this.activityFee = activityFee;
    }

    public Long getSpaId() {
        return spaId;
    }

    public void setSpaId(Long spaId) {
        this.spaId = spaId;
    }

    public ServiceProviderServices getServices() {
        return services;
    }

    public void setServices(ServiceProviderServices services) {
        this.services = services;
    }

    public MedActivities getActivities() {
        return activities;
    }

    public void setActivities(MedActivities activities) {
        this.activities = activities;
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
}

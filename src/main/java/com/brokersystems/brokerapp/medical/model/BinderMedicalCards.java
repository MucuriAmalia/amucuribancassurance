package com.brokersystems.brokerapp.medical.model;

import com.brokersystems.brokerapp.setup.model.BinderDetails;
import com.brokersystems.brokerapp.setup.model.BindersDef;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 13/04/2018.
 */
@Entity
@Table(name="sys_brk_binder_med_cards")
public class BinderMedicalCards {
    @javax.persistence.Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="crd_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="crd_bd_code",nullable=false)
    private BindersDef binder;

    @ManyToOne
    @JoinColumn(name="crd_card_code",nullable=false)
    private CardTypes cardTypes;


    @Column(name="crd_fee")
    private BigDecimal cardFee;

    @Column(name="crd_reissue_fee")
    private BigDecimal cardReissueFee;

    @Column(name="crd_service_charge")
    private BigDecimal serviceCharge;

    @Column(name="crd_service_prorated")
    private String serviceProrated;


    @Column(name="crd_vat_applicable")
    private String vatApplicable;


    @Column(name="crd_wef_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wefDate;

    @Column(name="crd_wet_date")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    private Date wetDate;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BindersDef getBinder() {
        return binder;
    }

    public void setBinder(BindersDef binder) {
        this.binder = binder;
    }

    public BigDecimal getCardFee() {
        return cardFee;
    }

    public void setCardFee(BigDecimal cardFee) {
        this.cardFee = cardFee;
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

    public CardTypes getCardTypes() {
        return cardTypes;
    }

    public void setCardTypes(CardTypes cardTypes) {
        this.cardTypes = cardTypes;
    }

    public BigDecimal getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(BigDecimal serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getServiceProrated() {
        return serviceProrated;
    }

    public void setServiceProrated(String serviceProrated) {
        this.serviceProrated = serviceProrated;
    }

    public BigDecimal getCardReissueFee() {
        return cardReissueFee;
    }

    public void setCardReissueFee(BigDecimal cardReissueFee) {
        this.cardReissueFee = cardReissueFee;
    }


    public String getVatApplicable() {
        return vatApplicable;
    }

    public void setVatApplicable(String vatApplicable) {
        this.vatApplicable = vatApplicable;
    }
}

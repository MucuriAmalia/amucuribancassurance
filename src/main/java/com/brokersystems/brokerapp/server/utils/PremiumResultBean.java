package com.brokersystems.brokerapp.server.utils;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

/**
 * Created by HP on 9/22/2017.
 */
@Getter
@Setter
@ToString
public class PremiumResultBean {

    private  double premium;
    private  double commissionPremium;
    private  double quarterlypremium;
    private  double semiAnnualpremium;
    private  double annualpremium;
    private  double singlepremium;
    private  double premiumFull;
    private  double sumInsured;
    private  double taxRelief;
    private double topUpAmount;
    private double investment;

    public PremiumResultBean() {
    }

    public PremiumResultBean(double premium, double premiumFull, double sumInsured, double commissionPremium) {
        this.premium = premium;
        this.premiumFull = premiumFull;
        this.sumInsured = sumInsured;
        this.quarterlypremium = 0;
        this.semiAnnualpremium = 0;
        this.annualpremium = 0;
        this.singlepremium = 0;
        this.topUpAmount = 0;
        this.commissionPremium = commissionPremium;
    }

    public PremiumResultBean(double sumInsured, double premium, double commissionPremium) {
        this.sumInsured = sumInsured;
        this.premium = premium;
        this.commissionPremium = commissionPremium;
        this.premiumFull=0;
        this.quarterlypremium = 0;
        this.semiAnnualpremium = 0;
        this.annualpremium = 0;
        this.singlepremium = 0;
        this.topUpAmount = 0;
    }
}

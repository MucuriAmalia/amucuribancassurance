package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by peter on 04/12/2017.
 */
@Entity
@Table(name = "sys_brk_pol_benefits")
public class PolicyBenefitsDistribution {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "mat_id")
    private Long maturityId;

    @ManyToOne
    @JoinColumn(name = "mat_pol_id",nullable = false)
    private PolicyTrans policyId;

    @Column(name = "mat_yr")
    private String maturityYear;

    @Column (name = "mat_expected_dt")
    private Date maturityExpDate;

    @Column(name = "mat_est_benefit")
    private double estBenefit;

    @Column(name = "mat_accidental_death_benefit")
    private double immediateAccidentalDeathBenefit;
    @Column(name = "mat_accidental_disability_benefit")
    private double immediateAccidentalDisabilityBenefit;
    @Column(name = "mat_non_accidental_benefit")
    private double immediateNonAccidentalBenefit;
    @Column(name = "mat_partial_withdrawal_benefit")
    private double partialWithdrawalBenefit;
    @Column(name = "mat_critical_illness_benefit")
    private double criticalIllnessBenefit;
    @Column(name = "mat_paid_death_disability")
    private double paidupBenefitImmediatePayableOnDeathDisability;
    @Column(name = "mat_paid_total_benefit")
    private double paidupTotalBenefit;
    @Column(name = "mat_accidental_benefit")
    private double accidentalBenefit;
    @Column(name = "accumulatedPremium")
    private double accumulatedPremium;

    public double getAccumulatedPremium() {
        return accumulatedPremium;
    }

    public void setAccumulatedPremium(double accumulatedPremium) {
        this.accumulatedPremium = accumulatedPremium;
    }

    public double getImmediateAccidentalDeathBenefit() {
        return immediateAccidentalDeathBenefit;
    }

    public void setImmediateAccidentalDeathBenefit(double immediateAccidentalDeathBenefit) {
        this.immediateAccidentalDeathBenefit = immediateAccidentalDeathBenefit;
    }

    public double getImmediateAccidentalDisabilityBenefit() {
        return immediateAccidentalDisabilityBenefit;
    }

    public void setImmediateAccidentalDisabilityBenefit(double immediateAccidentalDisabilityBenefit) {
        this.immediateAccidentalDisabilityBenefit = immediateAccidentalDisabilityBenefit;
    }

    public double getImmediateNonAccidentalBenefit() {
        return immediateNonAccidentalBenefit;
    }

    public void setImmediateNonAccidentalBenefit(double immediateNonAccidentalBenefit) {
        this.immediateNonAccidentalBenefit = immediateNonAccidentalBenefit;
    }

    public double getPartialWithdrawalBenefit() {
        return partialWithdrawalBenefit;
    }

    public void setPartialWithdrawalBenefit(double partialWithdrawalBenefit) {
        this.partialWithdrawalBenefit = partialWithdrawalBenefit;
    }

    public double getCriticalIllnessBenefit() {
        return criticalIllnessBenefit;
    }

    public void setCriticalIllnessBenefit(double criticalIllnessBenefit) {
        this.criticalIllnessBenefit = criticalIllnessBenefit;
    }

    public double getPaidupBenefitImmediatePayableOnDeathDisability() {
        return paidupBenefitImmediatePayableOnDeathDisability;
    }

    public void setPaidupBenefitImmediatePayableOnDeathDisability(double paidupBenefitImmediatePayableOnDeathDisability) {
        this.paidupBenefitImmediatePayableOnDeathDisability = paidupBenefitImmediatePayableOnDeathDisability;
    }

    public double getPaidupTotalBenefit() {
        return paidupTotalBenefit;
    }

    public void setPaidupTotalBenefit(double paidupTotalBenefit) {
        this.paidupTotalBenefit = paidupTotalBenefit;
    }

    public double getAccidentalBenefit() {
        return accidentalBenefit;
    }

    public void setAccidentalBenefit(double accidentalBenefit) {
        this.accidentalBenefit = accidentalBenefit;
    }

    public Long getMaturityId() {
        return maturityId;
    }

    public void setMaturityId(Long maturityId) {
        this.maturityId = maturityId;
    }

    public PolicyTrans getPolicyId() {
        return policyId;
    }

    public void setPolicyId(PolicyTrans policyId) {
        this.policyId = policyId;
    }

    public String getMaturityYear() {
        return maturityYear;
    }

    public void setMaturityYear(String maturityYear) {
        this.maturityYear = maturityYear;
    }

    public Date getMaturityExpDate() {
        return maturityExpDate;
    }

    public void setMaturityExpDate(Date maturityExpDate) {
        this.maturityExpDate = maturityExpDate;
    }

    public double getEstBenefit() {
        return estBenefit;
    }

    public void setEstBenefit(double estBenefit) {
        this.estBenefit = estBenefit;
    }
}

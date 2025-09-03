package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * DTO for endowment policy projection details.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndowmentProjectionDetail {

    @JsonProperty("Year")
    private int year;

    @JsonProperty("ProjectionStartPaymentDate")
    private String projectionStartPaymentDate;

    @JsonProperty("SumAssured")
    private double sumAssured;

    @JsonProperty("MainBenefitPremium")
    private double mainBenefitPremium;

    @JsonProperty("HospitalisationBenefitPremium")
    private double hospitalisationBenefitPremium;

    @JsonProperty("ProjectionPeriodEndDate")
    private String projectionPeriodEndDate;

    @JsonProperty("AccumulatedPremium")
    private double accumulatedPremium;

    @JsonProperty("ImmediateAccidentalDeathBenefit")
    private double immediateAccidentalDeathBenefit;

    @JsonProperty("ImmediateAccidentalDisabilityBenefit")
    private double immediateAccidentalDisabilityBenefit;

    @JsonProperty("ImmediateNonAccidentalBenefit")
    private double immediateNonAccidentalBenefit;

    @JsonProperty("PartialWithdrawalBenefit")
    private double partialWithdrawalBenefit;

    @JsonProperty("CriticalIllnessBenefit")
    private double criticalIllnessBenefit;

    @JsonProperty("SurrenderValue")
    private double surrenderValue;

    @JsonProperty("MaturityBenefitPayment")
    private double maturityBenefitPayment;

    @JsonProperty("PaidupBenefitImmediatePayableOnDeathDisability")
    private double paidupBenefitImmediatePayableOnDeathDisability;

    @JsonProperty("PaidupAnnualMaturityBenefit")
    private double paidupAnnualMaturityBenefit;

    @JsonProperty("PaidupTotalBenefit")
    private double paidupTotalBenefit;

    @JsonProperty("FromDate")
    private String fromDate;

    @JsonProperty("ToDate")
    private String toDate;

    @JsonProperty("Premium")
    private double premium;

    @JsonProperty("AccidentalBenefit")
    private double accidentalBenefit;

    @JsonProperty("NonAccidentalBenefit")
    private double nonAccidentalBenefit;


    @Override
    public String toString() {
        return "EndowmentProjectionDetail{" +
                "year=" + year +
                ", projectionStartPaymentDate=" + projectionStartPaymentDate +
                ", sumAssured=" + sumAssured +
                ", mainBenefitPremium=" + mainBenefitPremium +
                ", hospitalisationBenefitPremium=" + hospitalisationBenefitPremium +
                ", projectionPeriodEndDate=" + projectionPeriodEndDate +
                ", accumulatedPremium=" + accumulatedPremium +
                ", immediateAccidentalDeathBenefit=" + immediateAccidentalDeathBenefit +
                ", immediateAccidentalDisabilityBenefit=" + immediateAccidentalDisabilityBenefit +
                ", immediateNonAccidentalBenefit=" + immediateNonAccidentalBenefit +
                ", partialWithdrawalBenefit=" + partialWithdrawalBenefit +
                ", criticalIllnessBenefit=" + criticalIllnessBenefit +
                ", surrenderValue=" + surrenderValue +
                ", maturityBenefitPayment=" + maturityBenefitPayment +
                ", paidupBenefitImmediatePayableOnDeathDisability=" + paidupBenefitImmediatePayableOnDeathDisability +
                ", paidupAnnualMaturityBenefit=" + paidupAnnualMaturityBenefit +
                ", paidupTotalBenefit=" + paidupTotalBenefit +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", premium=" + premium +
                ", accidentalBenefit=" + accidentalBenefit +
                ", nonAccidentalBenefit=" + nonAccidentalBenefit +
                '}';
    }
}
package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO for generating an education policy quote response.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateQuoteForEducationPolicyResponse {

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("Projections")
    private List<Projection> projections;

    @Override
    public String toString() {
        return "GenerateQuoteForEducationPolicyResponse{" +
                "currency='" + currency + '\'' +
                ", projections=" + projections +
                '}';
    }

    /**
     * Inner class representing a projection detail for an education policy quote.
     */
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Projection {

        @JsonProperty("ProjectionDate")
        private String projectionDate;

        @JsonProperty("Year")
        private double year;

        @JsonProperty("Premium")
        private double premium;

        @JsonProperty("AccumulatedPremium")
        private double accumulatedPremium;

        @JsonProperty("SumAssured")
        private double sumAssured;

        @JsonProperty("AccidentalBenefit")
        private double accidentalBenefit;

        @JsonProperty("NonAccidentalBenefit")
        private double nonAccidentalBenefit;

        @JsonProperty("SurrenderValue")
        private double surrenderValue;

        @JsonProperty("MaturityBenefitPayment")
        private double maturityBenefitPayment;

        @JsonProperty("PaidupBenefitPayableOnDeath")
        private double paidupBenefitPayableOnDeath;

        @JsonProperty("PaidupAnnualMaturityBenefit")
        private double paidupAnnualMaturityBenefit;

        @JsonProperty("PaidupTotalBenefit")
        private double paidupTotalBenefit;


        @Override
        public String toString() {
            return "Projection{" +
                    "projectionDate=" + projectionDate +
                    ", year=" + year +
                    ", premium=" + premium +
                    ", accumulatedPremium=" + accumulatedPremium +
                    ", sumAssured=" + sumAssured +
                    ", accidentalBenefit=" + accidentalBenefit +
                    ", nonAccidentalBenefit=" + nonAccidentalBenefit +
                    ", surrenderValue=" + surrenderValue +
                    ", maturityBenefitPayment=" + maturityBenefitPayment +
                    ", paidupBenefitPayableOnDeath=" + paidupBenefitPayableOnDeath +
                    ", paidupAnnualMaturityBenefit=" + paidupAnnualMaturityBenefit +
                    ", paidupTotalBenefit=" + paidupTotalBenefit +
                    '}';
        }
    }
}
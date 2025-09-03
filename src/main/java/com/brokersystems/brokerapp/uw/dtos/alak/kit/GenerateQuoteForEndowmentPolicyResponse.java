package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
/**
 * DTO for generating an endowment policy quote response.
 */
public class GenerateQuoteForEndowmentPolicyResponse {

    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("PolicyCommencementDate")
    private String policyCommencementDate;

    @JsonProperty("ProjectionPremiumEndDate")
    private String projectionPremiumEndDate;

    @JsonProperty("PremiumInLastYear")
    private double premiumInLastYear;

    @JsonProperty("TotalMaturityBenefits")
    private double totalMaturityBenefits;

    @JsonProperty("TotalMonthlyPremiumAmt")
    private double totalMonthlyPremiumAmt;

    @JsonProperty("Inflation")
    private double inflation;

    @JsonProperty("Frequency")
    private int frequency;

    @JsonProperty("ProjectionDetails")
    private List<EndowmentProjectionDetail> projectionDetails;

    @JsonProperty("AnnualMaturityBenefit")
    private List<EndowmentAnnualMaturityBenefit> annualMaturityBenefit;



    @Override
    public String toString() {
        return "GenerateQuoteForEndowmentPolicyResponse{" +
                "currency='" + currency + '\'' +
                ", policyCommencementDate=" + policyCommencementDate +
                ", projectionPremiumEndDate=" + projectionPremiumEndDate +
                ", premiumInLastYear=" + premiumInLastYear +
                ", totalMaturityBenefits=" + totalMaturityBenefits +
                ", totalMonthlyPremiumAmt=" + totalMonthlyPremiumAmt +
                ", inflation=" + inflation +
                ", frequency=" + frequency +
                ", projectionDetails=" + projectionDetails +
                ", annualMaturityBenefit=" + annualMaturityBenefit +
                '}';
    }
}
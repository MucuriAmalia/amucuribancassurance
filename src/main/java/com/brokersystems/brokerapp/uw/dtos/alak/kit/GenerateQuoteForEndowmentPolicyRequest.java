package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * DTO for generating an endowment policy quote request.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GenerateQuoteForEndowmentPolicyRequest {

    @JsonProperty("Frequency")
    private int frequency;

    @JsonProperty("InceptionDate")
    private String inceptionDate;

    @JsonProperty("PremiumAtEntry")
    private double premiumAtEntry;

    @JsonProperty("InflationPercentage")
    private double inflationPercentage;

    @JsonProperty("PremiumPaymentTerms")
    private int premiumPaymentTerms;

    @JsonProperty("MaturityPeriod")
    private int maturityPeriod;

    @JsonProperty("AccidentalHospitalisationBenefit")
    private boolean accidentalHospitalisationBenefit;

    @JsonProperty("MedicalUWQ1")
    private boolean medicalUWQ1;

    @JsonProperty("MedicalUWQ2")
    private boolean medicalUWQ2;

    @JsonProperty("MedicalUWQ3")
    private boolean medicalUWQ3;

    @JsonProperty("MedicalUWQ4")
    private boolean medicalUWQ4;

    @JsonProperty("MainMemberBirthDate")
    private String mainMemberBirthDate;

    @JsonProperty("MainMemberGender")
    private int mainMemberGender;

    @JsonProperty("StaffDiscount")
    private boolean staffDiscount;


    @Override
    public String toString() {
        return "GenerateQuoteForEndowmentPolicyRequest{" +
                "frequency=" + frequency +
                ", inceptionDate=" + inceptionDate +
                ", premiumAtEntry=" + premiumAtEntry +
                ", inflationPercentage=" + inflationPercentage +
                ", premiumPaymentTerms=" + premiumPaymentTerms +
                ", maturityPeriod=" + maturityPeriod +
                ", accidentalHospitalisationBenefit=" + accidentalHospitalisationBenefit +
                ", medicalUWQ1=" + medicalUWQ1 +
                ", medicalUWQ2=" + medicalUWQ2 +
                ", medicalUWQ3=" + medicalUWQ3 +
                ", medicalUWQ4=" + medicalUWQ4 +
                ", mainMemberBirthDate=" + mainMemberBirthDate +
                ", mainMemberGender=" + mainMemberGender +
                ", staffDiscount=" + staffDiscount +
                '}';
    }
}
package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



/**
 * DTO for generating an education policy quote request.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GenerateQuoteForEducationPolicyRequest {

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

    @JsonProperty("MainMemberFullName")
    private String mainMemberFullName;

    @JsonProperty("MainMemberBirthDate")
    private String mainMemberBirthDate;

    @JsonProperty("MedicalUWQ1")
    private boolean medicalUWQ1;

    @JsonProperty("MedicalUWQ2")
    private boolean medicalUWQ2;

    @JsonProperty("MedicalUWQ3")
    private int medicalUWQ3;

    @JsonProperty("MedicalUWQ4")
    private boolean medicalUWQ4;

    @JsonProperty("MedicalUWQ5")
    private String medicalUWQ5;

    @JsonProperty("StaffDiscount")
    private boolean staffDiscount;


    @Override
    public String toString() {
        return "GenerateQuoteForEducationPolicyRequest{" +
                "frequency=" + frequency +
                ", inceptionDate=" + inceptionDate +
                ", premiumAtEntry=" + premiumAtEntry +
                ", inflationPercentage=" + inflationPercentage +
                ", premiumPaymentTerms=" + premiumPaymentTerms +
                ", maturityPeriod=" + maturityPeriod +
                ", mainMemberFullName='" + mainMemberFullName + '\'' +
                ", mainMemberBirthDate=" + mainMemberBirthDate +
                ", medicalUWQ1=" + medicalUWQ1 +
                ", medicalUWQ2=" + medicalUWQ2 +
                ", medicalUWQ3='" + medicalUWQ3 + '\'' +
                ", medicalUWQ4=" + medicalUWQ4 +
                ", medicalUWQ5='" + medicalUWQ5 + '\'' +
                ", staffDiscount=" + staffDiscount +
                '}';
    }
}
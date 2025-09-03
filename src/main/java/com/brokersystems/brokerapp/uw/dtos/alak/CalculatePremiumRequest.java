package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculatePremiumRequest {
    private int Frequency;
    private String InceptionDate;
    private int SumAssuredAtEntry;
    private double InflationPercentage;
    private int PremiumPaymentTerms;
    private int MaturityPeriod;
    private boolean AccidentalHospitalisationBenefit;
    private boolean MedicalUWQ1;
    private boolean MedicalUWQ2;
    private boolean MedicalUWQ3;
    private boolean MedicalUWQ4;
    private String MainMemberBirthDate;
    private int MainMemberGender;
    private boolean StaffDiscount;
}

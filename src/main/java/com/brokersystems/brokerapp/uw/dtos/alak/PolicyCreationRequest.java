package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyCreationRequest {
    private PolicyBasicDetails PolicyBasicDetails;
    private String StaffId;
    private boolean StaffDiscount;
    private double InflationPercentage;
    private BeneficiaryDetails Beneficiary1;
    private BeneficiaryDetails Beneficiary2;
    private BeneficiaryDetails Beneficiary3;
    private BeneficiaryDetails Beneficiary4;
    private BeneficiaryDetails Beneficiary5;
    private boolean AccidentalHospitalisationBenefit;
    private boolean MedicalUWQ1;
    private ClientDetails ClientDetails;
    private int SumAssuredAtEntry;
    private boolean MedicalUWQ4;
    private String TaxPinNo;
    private boolean MedicalUWQ3;
    private boolean MedicalUWQ2;
    private boolean MedicalUWQ5;
    private int MaturityPeriod;
    private int PremiumPaymentTerms;
    private String ApplicationSignDate;
    private String ApplicationReceivedDate;
}

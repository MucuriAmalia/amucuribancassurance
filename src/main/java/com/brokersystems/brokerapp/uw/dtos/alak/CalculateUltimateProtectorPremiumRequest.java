package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateUltimateProtectorPremiumRequest {
    private int Frequency;
    private String InceptionDate;
    private int CoverOptionId;
    private int SumInsured;
    private Boolean AccidentalHospitalisationBenefit;
    private Boolean AccidentalOnlyPlan;
    private String MainMemberFullName;
    private String MainMemberBirthDate;
    private int Gender;
    private int MedicalUWQ1;
    private  int MedicalUWQ2;
    private  int MedicalUWQ3;
    private String SpouseFullName;
    private Child child1;
    private Child child2;
    private Child child3;
    private Child child4;
    private Child child5;


    @Data
    public static class Child{
        private String FullName;
        private String BirthDate;
        private Boolean StillBorn;
        private Boolean Student;
    }
}

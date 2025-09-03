package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalculatePersonalAccidentPremiumRequest {
    private int Frequency;
    private String InceptionDate;
    private int CoverOptionId;
    private int SumInsured;
    private boolean AccidentalHospitalisationBenefit;
    private String MainMemberFullName;
    private String MainMemberBirthDate;
    private String SpouseFullName;
    private Child Child1;
    private Child Child2;
    private Child Child3;
    private Child Child4;
    private Child Child5;


    @Data
    public static class Child{
        private String FullName;
        private String BirthDate;
        private boolean StillBorn;
        private boolean Student;
    }
}

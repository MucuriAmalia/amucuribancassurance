package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalculateFamilyProtectionPremiumRequest {
    private int Frequency;
    private String InceptionDate;
    private int FuneralCoverOptionId;
    private double SumInsured;
    private boolean IncomeBenefit;
    private String MainMemberFullName;
    private String MainMemberBirthDate;
    private String SpouseFullName;


    private Parent Parent1;
    private Parent Parent2;
    private Parent Parent3;
    private Parent Parent4;
    private Parent Parent5;

    private Child Child1;
    private Child Child2;
    private Child Child3;
    private Child Child4;
    private Child Child5;


    private EFM EFM1;
    private EFM EFM2;
    private EFM EFM3;
    private EFM EFM4;
    private EFM EFM5;
    private EFM EFM6;
    private EFM EFM7;
    private EFM EFM8;

    @Data
    public static class Child {
        private String FullName;
        private String BirthDate;
    }

    @Data
    public static class Parent {
        private String FullName;
        private String BirthDate;
        private double SumInsured;
    }

    @Data
    public static class EFM {
        private String FullName;
        private String BirthDate;
    }
}

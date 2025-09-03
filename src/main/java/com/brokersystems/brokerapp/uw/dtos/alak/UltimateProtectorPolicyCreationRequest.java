package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UltimateProtectorPolicyCreationRequest {
    private ClientDetailsDto ClientDetails;
    private PolicyBasicDetailsDto PolicyBasicDetails;
    private PolicyBankingDataDto PolicyBankingData;
    private BeneficiaryDto Beneficiary1;
    private String ApplicationSignDate;
    private String ApplicationReceivedDate;
    private int CoverOption;
    private int CoverPlan;
    private int ManualSumInsured;
    private int InflationPercentage;
    private String TaxPinNo;
    private boolean StaffDiscount;
    private boolean AccidentalHospitalisationBenefit;
    private boolean AccidentalOnlyPlan;
    private int MedicalUWQ1;
    private int MedicalUWQ2;
    private int MedicalUWQ3;
    private SpouseDataDto SpouseData;
    private  ChildDataDto ChildData;

    @Data
    public static class ChildDataDto {
        private String Initials;
        private String FirstName;
        private String Surname;
        private String DOB;
        private int IDType;
        private String IdNumber;
        private int Gender;
        private boolean Student;
        private boolean StillBorn;
    }
    @Data
    public static class SpouseDataDto {
        private String Initials;
        private String FirstName;
        private String Surname;
        private LocalDateTime DOB;
        private int IDType;
        private String IdNumber;
    }

    @Data
    public static class ClientDetailsDto {
        private int ClientGroupID;
        private String CompanyName;
        private String Initials;
        private String Title;
        private String Surname;
        private String FirstName;
        private String BirthDate;
        private String IDNumber;
        private int IdentificationTypeID;
        private int MaritalStatusID;
        private String Gender;
        private int CitizenCountryID;
        private int ResidenceCountryID;
        private String CoReg;
        private String PostalAddress1;
        private String PostalAddress2;
        private String PostalAddress3;
        private String PostalAddress4;
        private String PostalCode;
        private String ResidentialAddress1;
        private String ResidentialAddress2;
        private String ResidentialAddress3;
        private String ResidentialAddress4;
        private String ResidentialCode;
        private String TelWork;
        private String TelHome;
        private String TelCell;
        private String AltTelCel;
        private String Fax;
        private String EmailID;
        private String URL;
        private String EmploymentDate;
        private String Employer;
        private String EmployeeNumber;
        private String Occupation;
        private int SourceOfFundsID;
        private String SourceOfFundsOthers;
        private int SourceOfIncomeID;
        private String SourceOfIncomeOthers;
        private String IndustryCode;
        private String EmploymentStatusCode;
        private String GrossSalary;
        private boolean PreferredMethodSMS;
        private boolean PreferredMethodEmail;
        private boolean PreferredMethodCollect;
        private boolean PreferredMethodPost;
        private int ScreeningStatusID;
        private String ScreeningDate;
        private String ScreeningExpiryDate;
        private String ScreeningComment;
    }

    @Data
    public static class PolicyBasicDetailsDto {
        private int PolicyTypeID;
        private int PolicyClassID;
        private int BranchID;
        private int BrokerID;
        private int Frequency;
        private String InceptionDate;
        private int BrokerRefID;
        private int SalesChannel;
    }

    @Data
    public static class PolicyBankingDataDto {
        private int CurrencyID;
        private String AccountNumber;
        private String AccountName;
        private String Description;
        private String AccountMapRef;
        private int BankID;
        private int BankBranchID;
        private int AccountTypeID;
        private String SwiftCode;
        private int StrikeDay;
        private String AdditionalRef;
        private int PaymentMethodID;
    }

    @Data
    public static class BeneficiaryDto {
        private String Initials;
        private String Title;
        private String Surname;
        private String FirstName;
        private String BirthDate;
        private String Gender;
        private int IDType;
        private String IDNumber;
        private int CitizenCountryID;
        private int RelationID;
        private int BeneficiaryPortion;
    }
}
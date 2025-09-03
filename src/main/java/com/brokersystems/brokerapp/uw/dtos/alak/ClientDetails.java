package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientDetails {
    private int ResidenceCountryID;
    private String EmailID;
    private boolean PreferredMethodSMS;
    private String TelHome;
    private int GrossSalary;
    private int MaritalStatusID;
    private int ClientGroupID;
    private boolean PreferredMethodPost;
    private String PostalCode;
    private String Gender;
    private String TelCell;
    private boolean PreferredMethodCollect;
    private int CompanyName;
    private String AltTelCel;
    private boolean PreferredMethodEmail;
    private String IDNumber;
    private String IndustryCode;
    private int CitizenCountryID;
    private String FirstName;
    private String Employer;
    private int IdentificationTypeID;
    private String Title;
    private String Initials;
    private String TelWork;
    private String EmploymentStatusCode;
    private String Occupation;
    private int SourceOfFundsID;
    private String SourceOfIncomeID;
    private String EmploymentDate;
    private String Surname;
    private String BirthDate;
    private String ResidentialAddress1;
    private String ResidentialAddress2;
    private String ResidentialAddress3;
    private String ResidentialAddress4;
    private String ResidentialAddress5;
    private String PostalAddress1;
    private String PostalAddress2;
    private String PostalAddress3;
    private String PostalAddress4;
}

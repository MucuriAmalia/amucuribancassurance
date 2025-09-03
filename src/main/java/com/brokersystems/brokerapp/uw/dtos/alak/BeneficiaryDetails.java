package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BeneficiaryDetails {
    private int BeneficiaryPortion;
    private int CitizenCountryID;
    private String FirstName;
    private int RelationID;
    private String Title;
    private String Gender;
    private String Initials;
    private String SurName;
    private String BirthDate;
    private String IDNumber;

}

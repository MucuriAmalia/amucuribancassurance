package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetSumInsuredRequest {
    private String IdNumber;
    private String FirstName;
    private String Surname;
    private String BirthDate;
}

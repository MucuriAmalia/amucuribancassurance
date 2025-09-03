package com.brokersystems.brokerapp.uw.dtos.alak.kit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * DTO for endowment policy annual maturity benefit details.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndowmentAnnualMaturityBenefit {

    @JsonProperty("strText")
    private String strText;

    @JsonProperty("AnnualMaturityBenefit")
    private double annualMaturityBenefit;

    @JsonProperty("MaturityDate")
    private String maturityDate;

    @Override
    public String toString() {
        return "EndowmentAnnualMaturityBenefit{" +
                "strText='" + strText + '\'' +
                ", annualMaturityBenefit=" + annualMaturityBenefit +
                ", maturityDate=" + maturityDate +
                '}';
    }
}
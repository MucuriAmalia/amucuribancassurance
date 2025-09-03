package com.brokersystems.brokerapp.uw.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SectionTransDTO {

    private Long sectId;
    private Long sclCode;
    private Long sectionSectId;
    private Long riskId;
    private Long polId;
    private Long premRatesId;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal divFactor;
    private BigDecimal freeLimit;
    private BigDecimal prem;
    private BigDecimal calcprem;
}

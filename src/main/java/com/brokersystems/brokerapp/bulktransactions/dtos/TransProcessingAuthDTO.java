package com.brokersystems.brokerapp.bulktransactions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransProcessingAuthDTO {
    private Long policyId;
    private  String loadedCoverType;
    private  String polNo;
    private BigDecimal sumInsured;
    private BigDecimal basicPrem;
    private Date coverFrom;
    private Date coverTo;
    private Date dateProcessed;
}

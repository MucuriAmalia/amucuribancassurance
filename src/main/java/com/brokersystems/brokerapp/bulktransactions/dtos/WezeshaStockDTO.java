package com.brokersystems.brokerapp.bulktransactions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class WezeshaStockDTO {

    private Long wezeshaStockId;
    private String loanId;
    private String clientFname;
    private String clientOtherNames;
    private String businessType;
    private String coverType;
    private BigDecimal stockSumInsured;
    private BigDecimal stockPremium;
    private Date startDate;
    private Date endDate;
    private Date uploadedDate;
    private Long polId;
    private String polNo;
    private String fullName;
    private Date polDate;
    private String polStatus;
}

package com.brokersystems.brokerapp.trans.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AllCommissionsDTO {

    private BigDecimal commission;
    private BigDecimal subAgentComm;
    private BigDecimal marketerComm;
    private BigDecimal adminFeeTotal;
    private BigDecimal adminFeeWhtx;
    private BigDecimal whtx;
    private BigDecimal balance;
    private Integer installmentNo;
    private Long transactionId;
    private Long prevTransactionId;


}

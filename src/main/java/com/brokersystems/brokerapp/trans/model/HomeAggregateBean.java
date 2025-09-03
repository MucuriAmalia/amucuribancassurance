package com.brokersystems.brokerapp.trans.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Created by peter on 2/22/2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HomeAggregateBean {

    private BigDecimal premiumYTD;
    private BigDecimal sumAssuredYTD;
    private Long policiesSold;

}

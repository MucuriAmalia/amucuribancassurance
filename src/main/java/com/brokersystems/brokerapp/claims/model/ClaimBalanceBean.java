package com.brokersystems.brokerapp.claims.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Created by HP on 9/29/2017.
 */
@Setter
@Getter
@Slf4j
public class ClaimBalanceBean {

    private BigDecimal clientBalance;
    private BigDecimal insBalance;

    @Override
    public String toString() {
        return "ClaimBalanceBean{" +
                "clientBalance=" + clientBalance +
                ", insBalance=" + insBalance +
                '}';
    }
}

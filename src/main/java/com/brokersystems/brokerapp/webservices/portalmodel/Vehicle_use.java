
package com.brokersystems.brokerapp.webservices.portalmodel;

import java.math.BigDecimal;

/**
 * Created by HP on 6/12/2018.
 */
public class Vehicle_use {

    private BigDecimal id;
    private String name;
    private String alias;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}

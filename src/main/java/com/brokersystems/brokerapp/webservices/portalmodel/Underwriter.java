
package com.brokersystems.brokerapp.webservices.portalmodel;

import java.math.BigDecimal;

/**
 * Created by HP on 6/20/2018.
 */
public class Underwriter {


    private BigDecimal id;
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }
}
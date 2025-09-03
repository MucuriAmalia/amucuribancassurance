
package com.brokersystems.brokerapp.webservices.portalmodel;

import java.math.BigDecimal;

/**
 * Created by HP on 6/12/2018.
 */
public class Cover_Type {

    private String name;
    private BigDecimal id;
    private Insurance_type insurance_type;
    private boolean primary;
    private boolean optional_benefit;
    private boolean benefit;


    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Insurance_type getInsurance_type() {
        return insurance_type;
    }

    public void setInsurance_type(Insurance_type insurance_type) {
        this.insurance_type = insurance_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public boolean isOptional_benefit() {
        return optional_benefit;
    }

    public void setOptional_benefit(boolean optional_benefit) {
        this.optional_benefit = optional_benefit;
    }

    public boolean isBenefit() {
        return benefit;
    }

    public void setBenefit(boolean benefit) {
        this.benefit = benefit;
    }
}

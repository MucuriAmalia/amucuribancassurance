
package com.brokersystems.brokerapp.webservices.portalmodel;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by HP on 6/12/2018.
 */
public class Product {

    private BigDecimal id;
    private String name;
    private List<Benefits> benefits;
    private List<Binders> binders;
    private Cover_Type cover_type;

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

    public List<Benefits> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<Benefits> benefits) {
        this.benefits = benefits;
    }

    public List<Binders> getBinders() {
        return binders;
    }

    public void setBinders(List<Binders> binders) {
        this.binders = binders;
    }


    public Cover_Type getCover_type() {
        return cover_type;
    }

    public void setCover_type(Cover_Type cover_type) {
        this.cover_type = cover_type;
    }
}
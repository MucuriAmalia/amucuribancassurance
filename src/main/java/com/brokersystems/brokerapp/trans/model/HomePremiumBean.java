package com.brokersystems.brokerapp.trans.model;

import lombok.ToString;

import java.math.BigDecimal;

/**
 * Created by peter on 2/23/2017.
 */
@ToString
public class HomePremiumBean {

    private String month;
    private double year;
    private BigDecimal premium;
    private String product;
    private String branch;

    public HomePremiumBean(String month, double year, BigDecimal premium) {
        this.month = month;
        this.year = year;
        this.premium = premium;
    }

    public HomePremiumBean(){

    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public double getYear() {
        return year;
    }

    public void setYear(double year) {
        this.year = year;
    }

    public BigDecimal getPremium() {
        return premium;
    }

    public void setPremium(BigDecimal premium) {
        this.premium = premium;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}

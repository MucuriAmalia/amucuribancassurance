package com.brokersystems.brokerapp.server.utils;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by waititu on 04/12/2017.
 */
public class MaturitiesBean {

    private String benefitYear;

    private double estBenefit;

    private Long policyId;

    private Date expecteddate;



    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getBenefitYear() {
        return benefitYear;
    }

    public Date getExpecteddate() {
        return expecteddate;
    }

    public void setExpecteddate(Date expecteddate) {
        this.expecteddate = expecteddate;
    }

    public void setBenefitYear(String benefitYear) {
        this.benefitYear = benefitYear;
    }

    public double getEstBenefit() {
        return estBenefit;
    }

    public void setEstBenefit(double estBenefit) {
        this.estBenefit = estBenefit;
    }

}

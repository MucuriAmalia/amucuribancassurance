package com.brokersystems.brokerapp.uw.dtos;

import java.util.List;

/**
 * Created by waititu on 01/10/2017.
 */
public class TaxesDTO {

    private Long taxPolicyCode;
    private List<Long> taxes;

    public Long getTaxPolicyCode() {
        return taxPolicyCode;
    }

    public void setTaxPolicyCode(Long taxPolicyCode) {
        this.taxPolicyCode = taxPolicyCode;
    }

    public List<Long> getTaxes() {
        return taxes;
    }

    public void setTaxes(List<Long> taxes) {
        this.taxes = taxes;
    }
}

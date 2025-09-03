package com.brokersystems.brokerapp.medical.model;

import java.util.List;

/**
 * Created by peter on 5/28/2017.
 */
public class MedicalCoverDTO {

    private Long catCode;
    private List<Long> benefits;

    public Long getCatCode() {
        return catCode;
    }

    public void setCatCode(Long catCode) {
        this.catCode = catCode;
    }

    public List<Long> getBenefits() {
        return benefits;
    }

    public void setBenefits(List<Long> benefits) {
        this.benefits = benefits;
    }
}

package com.brokersystems.brokerapp.uw.dtos;

import java.math.BigDecimal;

public class MileageDTO {

    private BigDecimal mileage;
    private BigDecimal start_mileage;
    private BigDecimal used_mileage;

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public BigDecimal getStart_mileage() {
        return start_mileage;
    }

    public void setStart_mileage(BigDecimal start_mileage) {
        this.start_mileage = start_mileage;
    }

    public BigDecimal getUsed_mileage() {
        return used_mileage;
    }

    public void setUsed_mileage(BigDecimal used_mileage) {
        this.used_mileage = used_mileage;
    }
}

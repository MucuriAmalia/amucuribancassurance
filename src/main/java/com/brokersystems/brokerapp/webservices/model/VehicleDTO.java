package com.brokersystems.brokerapp.webservices.model;

import java.math.BigDecimal;

public class VehicleDTO {

    private String numberPlate;
    private String model;
    private String color;
    private String make;
    private BigDecimal engineSize;
    private Long yearOfManufacture;
    private String chassisNumber;

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public BigDecimal getEngineSize() {
        return engineSize;
    }

    public void setEngineSize(BigDecimal engineSize) {
        this.engineSize = engineSize;
    }

    public Long getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(Long yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }
}

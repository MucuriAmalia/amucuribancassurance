package com.brokersystems.brokerapp.uw.model;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="sys_brk_vehicle_details")
public class MotorVehicleDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="vd_id")
    private Long vdId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="vd_risk_id",nullable = false)
    private RiskTrans risk;

    @Column(name="vd_log_book",length = 30)
    private String logbookNumber;

    @Column(name="vd_color",length = 50)
    private String bodyColor;

    @Column(name="vd_engine_no",length = 50)
    private String engineNumber;

    @Column(name="vd_chassis_no",length = 50)
    private String chassisNumber;

    @Column(name="vd_carry_capacity")
    private BigDecimal carryCapacity;

    @Column(name="vd_cc")
    private BigDecimal engineCapacity;

    @Column(name="vd_mileage")
    private BigDecimal mileage;

    @Column(name="vd_yom",nullable = false)
    private Long yearOfManufacture;

    @Column(name="vd_model",length = 30)
    private String carModel;

    @Column(name="vd_make",length = 30)
    private String carMake;

    @Column(name="vd_body_type",length = 50)
    private String bodyType;

    @Column(name="vd_fuel_type",length = 50)
    private String fuelType;

    public RiskTrans getRisk() {
        return risk;
    }

    public void setRisk(RiskTrans risk) {
        this.risk = risk;
    }

    public Long getVdId() {
        return vdId;
    }

    public void setVdId(Long vdId) {
        this.vdId = vdId;
    }

    public String getLogbookNumber() {
        return logbookNumber;
    }

    public void setLogbookNumber(String logbookNumber) {
        this.logbookNumber = logbookNumber;
    }

    public String getBodyColor() {
        return bodyColor;
    }

    public void setBodyColor(String bodyColor) {
        this.bodyColor = bodyColor;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getChassisNumber() {
        return chassisNumber;
    }

    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }

    public BigDecimal getCarryCapacity() {
        return carryCapacity;
    }

    public void setCarryCapacity(BigDecimal carryCapacity) {
        this.carryCapacity = carryCapacity;
    }

    public BigDecimal getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(BigDecimal engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public BigDecimal getMileage() {
        return mileage;
    }

    public void setMileage(BigDecimal mileage) {
        this.mileage = mileage;
    }

    public Long getYearOfManufacture() {
        return yearOfManufacture;
    }

    public void setYearOfManufacture(Long yearOfManufacture) {
        this.yearOfManufacture = yearOfManufacture;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarMake() {
        return carMake;
    }

    public void setCarMake(String carMake) {
        this.carMake = carMake;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}

package com.brokersystems.brokerapp.webservices.portalmodel;

import java.math.BigDecimal;

public class MedicalDTO {

    private String name;
    private BigDecimal coverLimit;
    private boolean spouse;
    private String gender;
    private double minAgeMonths;
    private double maxAgeMonths;
    private double minAgeYears;
    private double maxAgeYears;
    private Integer maxJoinAgeMonths;
    private double maxJoinAgeYears;
    private Integer medicExamAge;
    private double premium;
    private String cover;
    private Long coverId;
    private boolean primary;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCoverLimit() {
        return coverLimit;
    }

    public void setCoverLimit(BigDecimal coverLimit) {
        this.coverLimit = coverLimit;
    }

    public boolean isSpouse() {
        return spouse;
    }

    public void setSpouse(boolean spouse) {
        this.spouse = spouse;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public double getMinAgeMonths() {
        return minAgeMonths;
    }

    public void setMinAgeMonths(double minAgeMonths) {
        this.minAgeMonths = minAgeMonths;
    }

    public double getMaxAgeMonths() {
        return maxAgeMonths;
    }

    public void setMaxAgeMonths(double maxAgeMonths) {
        this.maxAgeMonths = maxAgeMonths;
    }


    public Integer getMaxJoinAgeMonths() {
        return maxJoinAgeMonths;
    }

    public void setMaxJoinAgeMonths(Integer maxJoinAgeMonths) {
        this.maxJoinAgeMonths = maxJoinAgeMonths;
    }

    public double getMaxJoinAgeYears() {
        return maxJoinAgeYears;
    }

    public void setMaxJoinAgeYears(double maxJoinAgeYears) {
        this.maxJoinAgeYears = maxJoinAgeYears;
    }

    public Integer getMedicExamAge() {
        return medicExamAge;
    }

    public void setMedicExamAge(Integer medicExamAge) {
        this.medicExamAge = medicExamAge;
    }

    public double getMinAgeYears() {
        return minAgeYears;
    }

    public void setMinAgeYears(double minAgeYears) {
        this.minAgeYears = minAgeYears;
    }

    public double getMaxAgeYears() {
        return maxAgeYears;
    }

    public void setMaxAgeYears(double maxAgeYears) {
        this.maxAgeYears = maxAgeYears;
    }

    public double getPremium() {
        return premium;
    }

    public void setPremium(double premium) {
        this.premium = premium;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Long getCoverId() {
        return coverId;
    }

    public void setCoverId(Long coverId) {
        this.coverId = coverId;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public String toString() {
        return "MedicalDTO{" +
                "name='" + name + '\'' +
                ", coverLimit=" + coverLimit +
                ", spouse=" + spouse +
                ", gender='" + gender + '\'' +
                ", minAgeMonths=" + minAgeMonths +
                ", maxAgeMonths=" + maxAgeMonths +
                ", minAgeYears=" + minAgeYears +
                ", maxAgeYears=" + maxAgeYears +
                ", maxJoinAgeMonths=" + maxJoinAgeMonths +
                ", maxJoinAgeYears=" + maxJoinAgeYears +
                ", medicExamAge=" + medicExamAge +
                '}'+"\n";
    }
}

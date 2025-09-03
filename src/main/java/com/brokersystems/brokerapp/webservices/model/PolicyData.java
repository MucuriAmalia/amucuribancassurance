package com.brokersystems.brokerapp.webservices.model;

public class PolicyData {

    private String insurance;
    private String cover_type;
    private String valid_from;
    private String expiry_date;
    private String applicant_pin;
    private String policy_number;
    private String chassis_number;
    private String registration_number;


    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getCover_type() {
        return cover_type;
    }

    public void setCover_type(String cover_type) {
        this.cover_type = cover_type;
    }

    public String getValid_from() {
        return valid_from;
    }

    public void setValid_from(String valid_from) {
        this.valid_from = valid_from;
    }

    public String getExpiry_date() {
        return expiry_date;
    }

    public void setExpiry_date(String expiry_date) {
        this.expiry_date = expiry_date;
    }

    public String getApplicant_pin() {
        return applicant_pin;
    }

    public void setApplicant_pin(String applicant_pin) {
        this.applicant_pin = applicant_pin;
    }

    public String getPolicy_number() {
        return policy_number;
    }

    public void setPolicy_number(String policy_number) {
        this.policy_number = policy_number;
    }

    public String getChassis_number() {
        return chassis_number;
    }

    public void setChassis_number(String chassis_number) {
        this.chassis_number = chassis_number;
    }

    public String getRegistration_number() {
        return registration_number;
    }

    public void setRegistration_number(String registration_number) {
        this.registration_number = registration_number;
    }
}

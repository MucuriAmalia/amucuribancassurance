package com.brokersystems.brokerapp.setup.enums;

import lombok.Getter;

/**
 * Defines the stages at which commission rates are applicable in the broker system.
 * This enum is used in the {@code GlobalCommissionRates} entity to specify whether a commission
 * rate applies to new business or renewals, supporting the centralized pricing table for standard
 * fees and commissions. Each value is associated with a short code (e.g., "NB" for New Business)
 * and a human-readable name for display and reporting purposes. The codes are stored as strings
 * in the database with a maximum length of 5 characters.
 */
@Getter
public enum CommissionRateApplicableAt {

    /**
     * Represents a commission rate applicable to new business transactions.
     */
    NB("NB", "New Business"),

    /**
     * Represents a commission rate applicable to renewal transactions.
     */
    RN("RN", "Renewal");

    private final String code;

    private final String name;

    /**
     * Constructs a new commission applicability stage with the specified code and name.
     *
     * @param code the short code for the applicability stage
     * @param name the human-readable name for the applicability stage
     */
    CommissionRateApplicableAt(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
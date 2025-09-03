package com.brokersystems.brokerapp.setup.enums;

import lombok.Getter;

/**
 * Defines the types of commission rates used in the broker system.
 * This enum is used in the {@code GlobalCommissionRates} entity to specify how a commission rate
 * is calculated (e.g., as a percentage, per-mile rate, or fixed amount). Each type is associated
 * with a single-character code for concise storage and processing, and a human-readable name for
 * display and reporting purposes, supporting the centralized pricing table for standard fees and
 * commissions.
 */
@Getter
public enum CommissionRateType {

    /**
     * Represents a commission rate calculated as a percentage of a value.
     */
    P("P", "Percentage"),

    /**
     * Represents a commission rate calculated per mile, typically for distance-based fees.
     */
    M("M", "Per Mile"),

    /**
     * Represents a fixed commission amount, independent of other variables.
     */
    A("A", "Amount");


    private final String code;


    private final String name;

    /**
     * Constructs a new commission rate type with the specified code and name.
     *
     * @param code the single-character code for the commission rate type
     * @param name the human-readable name for the commission rate type
     */
    CommissionRateType(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
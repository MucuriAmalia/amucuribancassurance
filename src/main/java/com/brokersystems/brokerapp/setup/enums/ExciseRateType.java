package com.brokersystems.brokerapp.setup.enums;

import lombok.Getter;

/**
 * Defines the types of excise rates used in the broker system.
 * This enum is used to specify how an excise rate is calculated, either as a percentage or a fixed amount.
 * Each type is associated with a code and a human-readable name for storage and display purposes,
 * supporting the centralized pricing table for standard fees and commissions.
 */
@Getter
public enum ExciseRateType {

    /**
     * Represents an excise rate calculated as a percentage of a value.
     */
    PERCENT("P", "Percent"),

    /**
     * Represents a fixed excise amount, independent of other variables.
     */
    AMOUNT("A", "Amount");


    private final String code;


    private final String name;

    /**
     * Constructs a new excise rate type with the specified code and name.
     *
     * @param code the single-character code for the excise rate type
     * @param name the human-readable name for the excise rate type
     */
    ExciseRateType(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
package com.brokersystems.brokerapp.setup.enums;

import lombok.Getter;

/**
 * Defines the types of accounts to which commission rates can be applied in the broker system.
 * This enum is used in the {@code GlobalCommissionRates} entity to categorize commission rates
 * by account type, supporting a centralized pricing table for standard fees and commissions.
 * Each account type is associated with a human-readable name used for reporting and display purposes.
 */
@Getter
public enum CommissionRateAccountType {

    /**
     * Represents a marketer account responsible for direct sales or marketing activities.
     */
    MARKETER("Marketer"),

    /**
     * Represents an introducer account that refers clients or business opportunities.
     */
    INTRODUCER("Introducer"),

    /**
     * Represents sub-agents who operate under primary agents or brokers.
     */
    SUB_AGENTS("Sub Agents");

    /**
     * The human-readable name of the account type, used for display and reporting.
     */
    private final String name;

    /**
     * Constructs a new account type with the specified name.
     *
     * @param name the human-readable name of the account type
     */
    CommissionRateAccountType(String name) {
        this.name = name;
    }
}
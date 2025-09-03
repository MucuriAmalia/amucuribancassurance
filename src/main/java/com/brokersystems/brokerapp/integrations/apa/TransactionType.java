package com.brokersystems.brokerapp.integrations.apa;

import lombok.Getter;

/**
 * Enum representing the transaction types for APA Motor Private API operations.
 */
public enum TransactionType {
    NB("NB", "New Policy"),
    RN("RN", "Renewal"),
    EN("EN", "Endorsement"),
    CN("CN", "Cancellation"),
    CO("CO", "Reversal");

    @Getter
    private final String code;
    @Getter
    private final String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

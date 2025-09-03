package com.brokersystems.brokerapp.trans.enums;

import lombok.Getter;

/**
 * Enum representing the transaction types for APA Motor Private API operations.
 */
public enum TransactionType {
    NEW_BUSINESS("NB", "New Policy"),
    RENEWAL("RN", "Renewal"),
    ENDORSEMENT("EN", "Endorsement"),
    CANCELLATION("CN", "Cancellation"),
    REVERSAL("CO", "Reversal");

    @Getter
    private final String code;
    @Getter
    private final String description;

    TransactionType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}

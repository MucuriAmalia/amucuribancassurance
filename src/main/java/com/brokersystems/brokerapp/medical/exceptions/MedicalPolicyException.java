package com.brokersystems.brokerapp.medical.exceptions;

/**
 * Created by peter on 4/26/2017.
 */
public class MedicalPolicyException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MedicalPolicyException(String message) {
        super(message);
    }
}

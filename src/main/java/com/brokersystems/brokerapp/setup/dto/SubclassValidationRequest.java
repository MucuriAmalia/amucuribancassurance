package com.brokersystems.brokerapp.setup.dto;

import java.util.List; /**
 * Request DTO for subclass validation
 */
public class SubclassValidationRequest {
    private List<Long> subclassIds;

    // Default constructor
    public SubclassValidationRequest() {}

    // Getters and setters
    public List<Long> getSubclassIds() { return subclassIds; }
    public void setSubclassIds(List<Long> subclassIds) { this.subclassIds = subclassIds; }
}

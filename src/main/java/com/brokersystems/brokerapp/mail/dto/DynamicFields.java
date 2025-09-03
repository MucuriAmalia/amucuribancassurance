package com.brokersystems.brokerapp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicFields {
    private String name;
    private String type;
    private String value;
}
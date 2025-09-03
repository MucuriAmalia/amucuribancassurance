package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentContent {
    private String base64Data;
    private DocumentFormat documentFormat;
}

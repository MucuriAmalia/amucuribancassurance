package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentsList {
    private String transactionId;
    private List<CaseFields> searchFields;
    private boolean selectDistinct;
}

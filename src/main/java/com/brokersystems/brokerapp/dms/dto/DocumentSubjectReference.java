package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentSubjectReference {
    private String caseType;
    private String caseNumber;
}

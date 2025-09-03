package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseCreation {
    private List<CaseFields> caseFields;
    private DocumentSubjectReference documentSubjectReference;
    private String transactionId;
    private List<Documents> documents;
}

package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CaseUpdate {
    private String action;
    private String transactionId;
    private DocumentSubjectReference documentSubjectReference;
    private List<CaseFields> caseFields;
    private List<Documents> documents;
}

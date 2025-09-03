package com.brokersystems.brokerapp.dms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Documents {
    private String documentTypeValue;
    private List<CaseFields> fields;
    private List<DocumentContent> documentContents;
    private String password;
}

package com.brokersystems.brokerapp.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReconciliationDTO {
    private String policyNumber;
    private String clientName;
    private String riskNoteNumber;
    private String underwriterPolicyNumber;
    private BigDecimal premium;
    private BigDecimal payableCommission;
    private BigDecimal paidCommission;
    private BigDecimal commission;
    private String status;
    private String unmatchedEntries;
    private Date reconDate;
}

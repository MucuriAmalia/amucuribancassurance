package com.brokersystems.brokerapp.dms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SybrinCases {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;
    @Column(name = "case_no")
    private String caseNumber;
    @Column(name = "case_type")
    private String caseType;
    @Column(name = "document_guid")
    private String documentGuid;
    @Column(name = "case_file_name")
    private String caseFileName;
    @Column(name = "case_client_id")
    private Long caseClientId;
    @Column(name = "case_prospect_id")
    private Long caseProspectId;
    @Column(name = "case_risk_id")
    private Long caseRiskId;
    @Column(name = "case_acct_id")
    private Long caseAcctId;
    @Column(name = "case_req_doc_id")
    private Long caseRequiredDocId;
    @Column(name = "case_claim_id")
    private Long caseClaimId;
}

package com.brokersystems.brokerapp.integrations.apa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class APAMotorRequest {
    private String transactionCode;
    private int policyCode;
    private String policyNo;
    private String binderPolicyNo;
    private String endorsementNo;
    private String coverFromDate;
    private String coverToDate;
    private String policyDate;
    private String productShortDescription;
    private double premiumAmount;
    private double fap;
    private double transactionLevy;
    private double phcf;
    private String branchShortDescription;
    private String agentShortDescription;
    private String currencyShortDescription;
    private String paymentFrequency;
    private String transType;
    private String riskNoteNumber;
    private double stampDuty;
    private double commissionAmount;
    private double otherDuties;
    private double withholdingTax;
    private String transactionRemarks;
    private String effectiveDate;
    private double nettPremium;

    // Limits of Liability
    private List<LimitOfLiability> limitsOfLiability;

    // Clauses (simple list of strings)
    private List<String> clauses;

    // Exclusions and Excesses (share structure)
    private List<NarratedValue> exclusions;
    private List<NarratedValue> excesses;

    // Documents
    private List<Document> documents;

    // Client Info (embedded)
    private ClientInfo clientInfo;

    // Risk Info
    private List<RiskInfo> riskInfo;

    // --- Inner Classes ---

    @Data
    public static class LimitOfLiability {
        private String value;
        private String narration;
    }

    @Data
    public static class NarratedValue {
        private String value;
        private String narration;
    }

    @Data
    public static class Document {
        private String documentName;
        private String document;
        private String fileType;
        private String documentType;
    }

    @Data
    public static class ClientInfo {
        private int code;
        private String shortDescription;
        private String name;
        private String otherNames;
        private String idRegistrationNumber;
        private String dateOfBirth;
        private String pin;
        private String physicalAddress;
        private String postalAddress;
        private String emailAddress;
        private String telephone1;
        private String telephone2;
        private String fax;
        private String status;
        private String branchCode;
        private String requestId;
        private String type;
        private String passportNo;
        private String occupation;
        private String smsTel;
        private String smsTel2;
        private String clientType;
        private String gender;
        private String country;
        private String town;
    }

    @Data
    public static class RiskInfo {
        private String riskId;
        private String riskDescription;
        private String coverFromDate;
        private String coverToDate;
        private double riskValue;
        private String binderShortDescription;
        private String coverTypeShortDescription;
        private String coverTypeDescription;
        private String subClassShortDescription;
        private String certificateNumber;
        private String certFromDate;
        private String certToDate;
        private double riskPremium;
        private int riskIpuCode;

        private List<RiskSection> riskSections;
        private ClientInfo clientInfo;  // reused
        private List<RiskAdditionalInfo> riskAdditionalInfo;
    }

    @Data
    public static class RiskSection {
        private String shortDescription;
        private double limitAmount;
        private String description;
        private double premRate;
    }

    @Data
    public static class RiskAdditionalInfo {
        private GinMotorPrivateSchLevel1 ginMotorPrivateSch_level1;
    }

    @Data
    public static class GinMotorPrivateSchLevel1 {
        private String mpsMake;
        private String mpsValue;
        private String mpsCoverType;
        private String mpsYrManft;
        private String mpsLogbook;
        private String mpsTonnage;
        private String mpsColor;
        private String mpsCarryCapacity;
        private String mpsRegNo;
        private String mpsEngineNo;
        private String mpsChasisNo;
        private String mpsBodyType;
        private String mpsCubicCapacity;
    }
}

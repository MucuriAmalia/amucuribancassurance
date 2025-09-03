package com.brokersystems.brokerapp.bulktransactions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkPolicyCreationDTO {
    private Long bulkPolicyId;
    private String polNumber;
    private String productName;
    private Date coverDateFrom;
    private Date coverDateTo;
    private Date polWef;
    private Date polWet;
    private Date renewDate;
    private String currency;
    private Date creationDate;
    private String clientPin;
    private String underwriter;
    private String insuredPin;
    private String riskId;
    private String riskDesc;
    private String coverType;
    private BigDecimal sumInsured ;
    private BigDecimal negotiatedPremium;
    private String transProcessed;
    private Date dateUploaded;
    private Date dateProcessed;
    private Long policyId;
    private BigDecimal balance;
    private String clientFname;
    private String clientOtherNames;
    private String clientIdNo;
    private String insuredFName;
    private String insuredOtherNames;
    private String insuredPhoneNo;
    private String insuredEmail;
    private String insuredIdNo;
    private String clientEmail;
    private String clientPhoneNo;
    private String clientType;
    private String insuredType;
    private Date clientDOB;
    private Date insuredDOB;
    private String clientCIF;
    private String insuredCIF;
    private String subAgnetABNo;
    private String marketerABNo;
    private String introducerABNo;
    private String leadsABNo;
    private BigDecimal premium;
    private String frequency;
    private Integer totalInstalments;
    private String policyCoverType;
    private String businessType;
    private BigDecimal polBasicPrem;
    private String policyStatus;
    private Date polCreationDate;
    private String polClientFname;
    private String polClientOtherNames;
    private String polProposalNo;
    private BigDecimal refundablePremium;
    private BigDecimal refundAmount;
    private String salesCode;
    private String salesAgent;
    private Date accrualInstDate;
    private Date bulkPolicyRenewalDate;
    private String newInterface;
    private String accrualPaymentType;
    private BigDecimal newPrem;
    private BigDecimal newSumInsured;
    private Date effectiveDate;
    private BigDecimal cancelationAmt;
    private String enRemarks;
}

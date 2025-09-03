package com.brokersystems.brokerapp.bulktransactions.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreditShieldDTO {

    private Long shieldId;
    private String clientFName;
    private String clientOtherNames;
    private String clientCIF;
    private Date clientDOB;
    private String clientPin;
    private String clientPhone;
    private String clientEmail;
    private String clientID;
    private String coverType;
    private String productGroup;
    private String productName;
    private Date transDate;
    private BigDecimal sumInsured;
    private BigDecimal cardPremium;
    private String frequency;
    private Date coverFrom;
    private Date coverTo;
    private Date uploadedDate;
    private Date processedDate;
    private String transStatus;
    private String transCode;
    private String transType;
    private String salesAgent;
    private String salesManager;
    private String salesCode;
    private String cardType;
    private String cardAccount;
    private Long polId;
    private String polNo;
    private String insurerName;
}

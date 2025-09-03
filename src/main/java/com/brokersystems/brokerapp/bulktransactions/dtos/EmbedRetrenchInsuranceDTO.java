package com.brokersystems.brokerapp.bulktransactions.dtos;

import com.brokersystems.brokerapp.setup.model.ClientTypes;
import com.brokersystems.brokerapp.setup.model.Country;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class EmbedRetrenchInsuranceDTO {

    private Long embedPackageId;
    private String serialNo;
    private String clientFName;
    private String clientOtherNames;
    private Date clientDOB;
    private String clientPin;
    private String clientEmail;
    private String clientPhone;
    private Country countryCode;
    private ClientTypes tenantType;
    private Date transDate;
    private String productGroup;
    private String productName;
    private String coverType;
    private String currencyCode;
    private String frequency;
    private String insurerCode;
    private Date coverFrom;
    private Date coverTo;
    private Date uploadedDate;
    private String insurerName;
    private String insuredFName;
    private String insuredOtherNames;
    private Date processedDate;
    private String polNo;
    private Long polId;
    private BigDecimal polBasicPrem;
    private String policyStatus;
    private Date polWet;
    private Date polWef;
}

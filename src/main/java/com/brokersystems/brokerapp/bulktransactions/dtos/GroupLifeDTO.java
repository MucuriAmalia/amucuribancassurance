package com.brokersystems.brokerapp.bulktransactions.dtos;

import com.brokersystems.brokerapp.setup.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupLifeDTO {

    private Long groupLifeId;
    private String serialNo;
    private String clientFName;
    private String clientOtherNames;
    private Date clientDOB;
    private String clientId;
    private String clientPin;
    private String clientEmail;
    private String clientPhone;
    private Date transDate;
    private String tranStatus;
    private String transType;
    private String clientCIF;
    private String productGroup;
    private String productName;
    private String coverType;
    private String frequency;
    private Date coverFrom;
    private Date processedDate;
    private User processedBy;
    private Date uploadDate;
    private Date coverTo;
    private String insurerName;
    private Long polId;
    private String polNo;
    private BigDecimal totPremium;
    private String polStatus;
}

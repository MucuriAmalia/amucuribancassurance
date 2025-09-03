package com.brokersystems.brokerapp.updatepolno.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateClientPolNoDTO {

    private Long updateId;
    private String polNo;
    private String riskNote;
    private String clientPol;
    private String clientPin;
    private String updateStatus;
    private Date uploadDate;
    private Date polauthDate;
    private Date processedDate;
    private String insurerCode;
    private String clientId;
    private String productName;
    private Long insurerId;
    private Long polId;
    private String clientName;
    private String underwriterTransCode;
}

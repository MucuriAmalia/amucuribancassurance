package com.brokersystems.brokerapp.uw.dtos.alak;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyBasicDetails {
    private int PolicyTypeID;
    private int PolicyClassID;
    private String InceptionDate;
    private int Frequency;
    private int BranchID;
    private int BrokerID;
    private int BrokerRefID;
    private int SalesChannel;
}

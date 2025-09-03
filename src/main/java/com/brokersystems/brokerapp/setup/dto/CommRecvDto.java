package com.brokersystems.brokerapp.setup.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommRecvDto {
    private Long underwriterId;
    private Long debitAccount;
    private Long creditAccount;
    private Long prodGroup;
}

package com.brokersystems.brokerapp.accounts.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.util.List;

@Setter
@Getter
@ToString
public class ProcessingBean {

    private List<InsPaymentBean> credits;
    private Long accountCode;
    private Long subaccountType;
    private Long curCode;
    private String selreceiptType;

}
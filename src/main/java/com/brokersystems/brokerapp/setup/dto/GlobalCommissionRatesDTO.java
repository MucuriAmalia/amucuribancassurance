package com.brokersystems.brokerapp.setup.dto;

import com.brokersystems.brokerapp.setup.enums.CommissionRateAccountType;
import com.brokersystems.brokerapp.setup.enums.CommissionRateApplicableAt;
import com.brokersystems.brokerapp.setup.enums.CommissionRateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GlobalCommissionRatesDTO {

    private Long commId;

    private Long productCode;

    private String productGroup;

    private String productName;

    private CommissionRateAccountType accountTypeName;

    private String accountType;

    private BigDecimal commRate;

    private boolean active;

    private String rateDesc;

    private BigDecimal commDivFactor;

    private CommissionRateType rateType;

    private BigDecimal commRangeFrom;

    private BigDecimal commRangeTo;

    private CommissionRateApplicableAt applicableAt;

}
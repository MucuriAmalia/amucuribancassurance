package com.brokersystems.brokerapp.setup.dto;

import com.brokersystems.brokerapp.setup.enums.AdminFeeRateType;
import com.brokersystems.brokerapp.setup.enums.ExciseRateType;
import com.brokersystems.brokerapp.setup.enums.VATRateType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GlobalAdminFeeSetupDTO {

    private Long fcId;

    private Long productCode;

    private String productGroup;

    private String productName;

    private BigDecimal adminFeeRate;

    private AdminFeeRateType adminFeeRateType;

    private BigDecimal vatRate;

    private VATRateType vateRateType;

    private BigDecimal exciseRate;

    private ExciseRateType exciseRateType;

    private String status;

}
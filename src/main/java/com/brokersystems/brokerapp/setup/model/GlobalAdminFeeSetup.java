package com.brokersystems.brokerapp.setup.model;


import com.brokersystems.brokerapp.setup.enums.AdminFeeRateType;
import com.brokersystems.brokerapp.setup.enums.ExciseRateType;
import com.brokersystems.brokerapp.setup.enums.VATRateType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name="sys_brk_global_admin_fee_config")
public class GlobalAdminFeeSetup extends AuditBaseEntity{

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="fc_id")
    private Long fcId;

    /**
     * The unique code identifying the product associated with this commission rate.
     * This field is mandatory and links the commission rate to a specific product.
     */
    @Column(name = "comm_product_code", nullable = false)
    private Long productCode;

    /**
     * The group to which the product belongs (e.g., Insurance, Investment).
     * This field is mandatory and used for categorizing products in reports.
     */
    @Column(name = "comm_product_group", nullable = false)
    private String productGroup;

    /**
     * The name of the product associated with this commission rate.
     * This field is mandatory and provides a human-readable identifier for the product.
     */
    @Column(name = "comm_product_name", nullable = false)
    private String productName;

    @Column(name="fc_admin_fee_rate")
    private BigDecimal adminFeeRate;

    @Column(name="fc_admin_fee_rt_type",length = 10)
    private AdminFeeRateType adminFeeRateType;

    @Column(name="fc_vat_rate")
    private BigDecimal vatRate;

    @Column(name="fc_vat_rate_type",length = 10)
    private VATRateType vateRateType;

    @Column(name="fc_excise_rate")
    private BigDecimal exciseRate;

    @Column(name="fc_excise_rate_type",length = 10)
    private ExciseRateType exciseRateType;

    @Column(name="fc_status",length = 10)
    private String status;

}

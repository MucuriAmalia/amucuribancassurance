package com.brokersystems.brokerapp.setup.model;

import com.brokersystems.brokerapp.setup.enums.CommissionRateAccountType;
import com.brokersystems.brokerapp.setup.enums.CommissionRateApplicableAt;
import com.brokersystems.brokerapp.setup.enums.CommissionRateType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Represents a global commission rate configuration for products in the broker system.
 * This entity defines standard fees and commissions applicable across products, stored in the
 * {@code sys_brk_global_comm_rates} table. It supports a centralized pricing model where products
 * reference these global rates by default. Product-specific pricing overrides can be defined at the
 * product level, taking precedence over this global configuration when present. This table supports
 * reporting on commission setups, as required for system reports generated on July 2, 2025.
 */
@Getter
@Setter
@Entity
@Table(name = "sys_brk_global_comm_rates")
public class GlobalCommissionRates extends AuditBaseEntity{

    /**
     * Unique identifier for the commission rate record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "comm_id")
    private Long commId;

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

    /**
     * The type of account to which the commission rate applies (e.g., MARKETER, INTRODUCER, SUB_AGENTS).
     * Stored as a string in the database for clarity and flexibility.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "comm_account_type_name", nullable = false)
    private CommissionRateAccountType accountTypeName;

    @Column(name = "comm_account_type", nullable = false)
    private String accountType;

    /**
     * The commission rate value, represented as a percentage, per-mile rate, or fixed amount,
     * depending on the {@code rateType}. This field is mandatory.
     */
    @Column(name = "comm_rate", nullable = false)
    private BigDecimal commRate;

    /**
     * Indicates whether the commission rate is active and applicable.
     * Defaults to false if not specified.
     */
    @Column(name = "comm_active")
    private boolean active;

    /**
     * A description of the commission rate, providing additional context (e.g., purpose or applicability).
     * Limited to 255 characters.
     */
    @Column(name = "comm_rate_desc", length = 255)
    private String rateDesc;

    /**
     * The division factor used to calculate the commission, often used for scaling or adjustment.
     * This field is mandatory.
     */
    @Column(name = "comm_div_fact", nullable = false)
    private BigDecimal commDivFactor;

    /**
     * The type of commission rate (e.g., PERCENTAGE, PER_MILE, AMOUNT).
     * Stored as a string in the database to indicate how the {@code commRate} is applied.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "comm_rate_type", nullable = false)
    private CommissionRateType rateType;

    /**
     * The lower bound of the range for which this commission rate is applicable.
     * This field is mandatory and used to determine eligibility for the rate.
     */
    @Column(name = "comm_range_from", nullable = false)
    private BigDecimal commRangeFrom;

    /**
     * The upper bound of the range for which this commission rate is applicable.
     * This field is mandatory and used to determine eligibility for the rate.
     */
    @Column(name = "comm_range_to", nullable = false)
    private BigDecimal commRangeTo;

    /**
     * Specifies when the commission rate is applicable (e.g., NEW_BUSINESS, RENEWAL).
     * Stored as a string with a maximum length of 5 characters.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "comm_applicable_at", length = 5)
    private CommissionRateApplicableAt applicableAt;
}
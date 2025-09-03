package com.brokersystems.brokerapp.setup.model;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Entity
@Table(name="sys_brk_admin_fee_config")
public class AdminFeeSetUp {


    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name="fc_id")
    private Long fcId;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="fc_bin_code",nullable=false)
    private BindersDef binder;

    @Column(name="fc_admin_fee_rate")
    private BigDecimal adminFeeRate;

    @Column(name="fc_admin_fee_rt_type",length = 10)
    private String adminFeeRateType;

    @Column(name="fc_vat_rate")
    private BigDecimal vatRate;

    @Column(name="fc_vat_rate_type",length = 10)
    private String vateRateType;

    @Column(name="fc_excise_rate")
    private BigDecimal exciseRate;

    @Column(name="fc_excise_rate_type",length = 10)
    private String exciseRateType;

    @Column(name="fc_status",length = 10)
    private String status;

}

package com.brokersystems.brokerapp.claims.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.math.BigDecimal;

@Setter
@Getter
@Entity
@Table(name = "sys_brk_clm_pymnts_dtls")
@Slf4j
public class ClaimPaymentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cpd_id")
    private Long paymentDetailId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="cpd_peril_id",nullable=false)
    private ClaimPerils claimPerils;

    @Column(name = "cpd_amount")
    private BigDecimal clmPymntAmount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="clm_pymt_spd_id")
    private ClaimPayments claimPayments;

}

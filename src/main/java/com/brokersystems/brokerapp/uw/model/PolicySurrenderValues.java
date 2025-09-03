package com.brokersystems.brokerapp.uw.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_brk_pol_surrender_values")
public class PolicySurrenderValues {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "surr_id")
    private Long surrenderId;

    @ManyToOne
    @JoinColumn(name = "surr_pol_id",nullable = false)
    private PolicyTrans policyId;

    @Column(name = "surrender_yr")
    private String surrenderYear;

    @Column(name = "surrender_value")
    private BigDecimal surrenderValue;
}

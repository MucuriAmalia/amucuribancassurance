package com.brokersystems.brokerapp.bulktransactions.models;

import com.brokersystems.brokerapp.setup.model.AuditBaseEntity;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Data
@Table(name = "sys_brk_create_bulk_policy_en_risks")
public class BulkPolicyENRisk {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "bulk_policy_risk_Id")
    private Long risksId;

    @Column(name = "bulk_policy_risk_serial_no", nullable = false)
    private String serialNo;

    @Column(name = "bulk_policy_risk_sections", columnDefinition = "TEXT")
    private String sections;

    @Column(name = "bulk_policy_risk_limit")
    private BigDecimal limit;

    @Column(name = "bulk_policy_risk_rate")
    private Double rate;

    @Column(name = "bulk_policy_risk_div_factor")
    private Double divFactor;

    @ManyToOne
    @JoinColumn(name = "bulk_policy_id", nullable = false)
    private BulkPolEnCreation bulkPolicy;

    @Column(name = "bulk_policy_risk_compute")
    private boolean riskCompute;

    @Column(name = "bulk_policy_risk_status")
    private String riskStatus;

    @Column(name = "bulk_policy_risk_code")
    private String riskCode;

}

package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "sys_brk_pol_payments")
@Data
public class MaturityPayoutSchedule {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pay_id")
    private Long payoutId;
    @ManyToOne
    @JoinColumn(name = "pay_pol_id",nullable = false)
    private PolicyTrans policyId;
    @Column(name = "pay_date")
    @Temporal(TemporalType.DATE)
    private Date expPayoutDate;
    @Column(name = "pay_amount")
    private double payoutAmount;

}
package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "sys_brk_life_installments")
public class PolicyInstallments {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pi_inst_id")
    private Long installmentsId;

    private Date dueDate;

    private String installPaid;

    @Column(name="pi_installment_no")
    private Long installmentNo;

    private BigDecimal installPrem;

    @XmlTransient
    @ManyToOne
    @JoinColumn(name="lrct_policy_id")
    private PolicyTrans policyTrans;

    private Date paidDate;
    @Column(name = "inst_notification_sent")
    private boolean notificationSent;

    @Column(name = "pi_expected_comm")
    private BigDecimal expectedCommission;

    @Column(name = "pi_expected_sub_comm")
    private BigDecimal expectedSubAgentCommission;

    @Column(name = "pi_expected_mlr_comm")
    private BigDecimal expectedMarkerterCommission;

    public Long getInstallmentNo() { return installmentNo; }

    public void setInstallmentNo(Long installmentNo) { this.installmentNo = installmentNo; }


}

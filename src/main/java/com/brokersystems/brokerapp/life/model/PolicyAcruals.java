package com.brokersystems.brokerapp.life.model;

import com.brokersystems.brokerapp.uw.model.PolicyTrans;
import lombok.Data;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "sys_brk_life_pi_accruals")
public class PolicyAcruals {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "pi_accr_id")
    private Long accrId;

    private Date dueDate;

    private String accrlPaid;

    @Column(name="lrct_policy_id")
    private Long policyTrans;

    private Date nextAccrualDate;

    @Column(name = "acc_notification_sent")
    private boolean notificationSent;

    @Column(name = "last_notification_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastNotificationDate;

}

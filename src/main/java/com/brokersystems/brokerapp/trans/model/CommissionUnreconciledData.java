package com.brokersystems.brokerapp.trans.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_brk_commission_unreconciled_data")
public class CommissionUnreconciledData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unrecon_data_id")
    private Long id;

    @Column(name = "unrecon_data_pol_no")
    private String policyNumber;

    @Column(name = "unrecon_data_pol_client")
    private String clientName;

    @Column(name = "unrecon_data_dr_ref_no")
    private String debitRefNo;

    @Column(name = "unrecon_data_cr_ref_no")
    private String creditRefNo;

    @Column(name = "revision_no")
    private String revisionNo;

    @Column(name = "unrecon_data_payment")
    private BigDecimal payment;

    @Column(name = "unrecon_data_pol_comm")
    private BigDecimal commission;

    @Column(name = "unrecon_data_pol_whtx")
    private BigDecimal withholdingTax;

    @Column(name = "unrecon_data_pol_total_comm")
    private BigDecimal payableCommission;

    @Column(name = "unrecon_data_reason", length = 2000)
    private String unreconciledReason; // Why it couldn't be reconciled

    @Column(name = "unrecon_data_system_pol_no")
    private String systemPolicyNumber; // If found but didn't match

    @Column(name = "unrecon_data_system_client")
    private String systemClientName; // If found but didn't match

    @Column(name = "unrecon_data_system_comm")
    private BigDecimal systemCommission; // If found but didn't match

    @Column(name = "unrecon_data_system_whtx")
    private BigDecimal systemWhtx; // If found but didn't match

    @Column(name = "unrecon_data_system_payment")
    private BigDecimal systemPayment; // If found but didn't match

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "unrecon_data_created_date")
    private Date createdDate;

    @Column(name = "unrecon_data_batch_ref")
    private String batchReference; // To group reconciliation attempts

    @Column(name = "unrecon_underwriter_code")
    private Long underwriterCode;
}

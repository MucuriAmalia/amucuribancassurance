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
    @Table(name = "sys_brk_commission_reconciliation_data")
public class CommissionReconciliationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comm_recon_data_id")
    private Long id;

    @Column(name = "comm_recon_data_pol_no")
    private String policyNumber;

    @Column(name = "comm_underwriter_code")
    private Long underwriterCode;

    @Column(name = "comm_recon_data_pol_client")
    private String clientName;

    @Column(name = "comm_recon_data_uw_policy")
    private String underWriterPolicyNo;

    @Column(name = "comm_recon_trans_code")
    private String transCode;

    @Column(name = "comm_recon_data_dr_ref_no")
    private String debitRefNo;

    @Column(name = "comm_recon_data_cr_ref_no")
    private String creditRefNo;

    @Column(name = "revision_no")
    private String revisionNo;

    @Column(name = "comm_recon_data_payment")
    private BigDecimal payment;

    @Column(name = "comm_recon_data_pol_comm")
    private BigDecimal commission;

    @Column(name = "comm_recon_data_pol_admin_fee")
    private BigDecimal adminFee;

    @Column(name = "comm_recon_data_pol_total_revenue")
    private BigDecimal totalRevenue;

    @Column(name = "comm_recon_data_pol_whtx")
    private BigDecimal withholdingTax;

    @Column(name = "comm_recon_data_pol_admin_whtx")
    private BigDecimal adminFeeWhtx;

    @Column(name = "comm_recon_data_pol_total_comm")
    private BigDecimal payableCommission;

    @Column(name = "comm_recon_data_status")
    private String status;

    @Column(name = "comm_recon_data_mismatch_details", length = 1000)
    private String mismatchDetails; // Store specific column mismatches

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "comm_recon_data_date")
    private Date reconDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "comm_recon_data_reconciled_date")
    private Date reconciledDate; // When reconciliation was performed
}

package com.brokersystems.brokerapp.reconciliation;

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
@Table(name = "sys_brk_reconciliation_data")
public class ReconciliationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recon_data_id")
    private Long id;
    @Column(name = "recon_data_pol_no",unique = true)
    private String policyNumber;
    @Column(name = "recon_data_pol_client")
    private String clientName;
    @Column(name = "recon_data_pol_risk_note_no",unique = true)
    private String riskNoteNumber;
    @Column(name = "recon_data_pol_underwriter_no")
    private String underwriterPolicyNumber;
    @Column(name = "recon_data_pol_prem")
    private BigDecimal premium;
    @Column(name = "recon_data_pol_total_comm")
    private BigDecimal payableCommission;
    @Column(name = "recon_data_pol_comm")
    private BigDecimal commission;
    @Column(name = "recon_data_pol_whtx")
    private BigDecimal adminFee;
    @Column(name = "recon_data_admin_fee")
    private BigDecimal withholdingTax;
    @Column(name = "recon_data_status")
    private String status;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "recon_data_date")
    private Date reconDate;
}

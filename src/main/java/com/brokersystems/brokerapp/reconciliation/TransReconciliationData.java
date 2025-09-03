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
@Table(name = "sys_brk_trans_reconciliation_data")
public class TransReconciliationData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recon_data_id")
    private Long id;
    @Column(name = "recon_data_pol_no")
    private String policyNumber;
    @Column(name = "recon_data_pol_client")
    private String clientName;
    @Column(name = "recon_data_pol_risk_note_no",unique = true)
    private String riskNoteNumber;
    @Column(name = "recon_data_pol_underwriter_no")
    private String underwriterPolicyNumber;
    @Column(name = "recon_underwriter_trans_code",unique = true)
    private String underwriterTransCode;
    @Column(name = "recon_data_pol_prem")
    private BigDecimal premium;
    @Column(name = "recon_data_settlement")
    private BigDecimal settlement;
    @Column(name = "recon_data_balance")
    private BigDecimal balance;
    @Column(name = "recon_data_status")
    private String status;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "recon_data_date")
    private Date reconDate;
}

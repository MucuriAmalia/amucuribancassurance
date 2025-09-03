package com.brokersystems.brokerapp.reconciliation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sys_brk_trans_reconciliation")
public class TransReconciliation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recon_id")
    private Long id;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "recon_data_id")
    private TransReconciliationData transReconciliationData;
    @Column(name = "recon_status")
    private String status;
    @Column(name = "recon_unmatched_records")
    private String unmatchedEntries;
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Temporal(TemporalType.DATE)
    @Column(name = "recon_date")
    private Date reconDate;
}

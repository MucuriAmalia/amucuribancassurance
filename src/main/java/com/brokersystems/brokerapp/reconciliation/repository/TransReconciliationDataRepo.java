package com.brokersystems.brokerapp.reconciliation.repository;

import com.brokersystems.brokerapp.reconciliation.TransReconciliationData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface TransReconciliationDataRepo extends PagingAndSortingRepository<TransReconciliationData, Long> {

    @Query(value = "SELECT d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, " +
            "d.recon_data_pol_underwriter_no, d.recon_underwriter_trans_code, d.recon_data_pol_prem, " +
            "d.recon_data_settlement, d.recon_data_balance, " +
            "COUNT(*) OVER() as total_count " +
            "FROM sys_brk_trans_reconciliation_data d " +
            "left join sys_brk_trans_reconciliation r on r.recon_data_id = d.recon_data_id " +
            "left join sys_brk_policies sbp on d.recon_data_pol_no = sbp.pol_no " +
            "left join sys_brk_accounts sba on sbp.pol_agent_id = sba.acct_id " +
            "WHERE (LOWER(d.recon_data_pol_client) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_no) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_underwriter_no) LIKE LOWER(:search)) " +
            "AND (d.recon_data_status = 'N') " +
            "AND (d.recon_data_date BETWEEN :dateFrom AND :dateTo) " +
            "AND (sbp.pol_agent_id = :accountCode OR :accountCode IS NULL) " +
            "AND (:unifiedSearch IS NULL OR :unifiedSearch = '' " +
            "    OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(CONCAT('%', :unifiedSearch, '%')) " +
            "    OR (LOWER(:unifiedSearch) IN ('reconciled', 'Y') AND r.recon_status = 'Reconciled') " +
            "    OR (LOWER(:unifiedSearch) IN ('not reconciled', 'Y', 'unreconciled') AND r.recon_status = 'Not Reconciled')) " +
            "GROUP BY d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, " +
            "d.recon_data_pol_underwriter_no, d.recon_underwriter_trans_code, d.recon_data_pol_prem, " +
            "d.recon_data_settlement, d.recon_data_balance " +
            "ORDER BY d.recon_data_pol_no DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findTransReconciliationData(@Param("search") String search,
                                               @Param("dateFrom") Date dateFrom,
                                               @Param("dateTo") Date dateTo,
                                               @Param("accountCode") Long accountCode,
                                               @Param("pageNo") int pageNo,
                                               @Param("limit") int limit,
                                               @Param("unifiedSearch") String unifiedSearch);

    TransReconciliationData findByRiskNoteNumber(String riskNoteNumber);
}

package com.brokersystems.brokerapp.reconciliation.repository;

import com.brokersystems.brokerapp.reconciliation.TransReconciliation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

@Repository
public interface TransReconciliationRepo extends PagingAndSortingRepository<TransReconciliation, Long> {
    @Query(value = "SELECT d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, " +
            "d.recon_data_pol_underwriter_no, d.recon_underwriter_trans_code, d.recon_data_pol_prem, " +
            "d.recon_data_settlement, d.recon_data_balance, r.recon_status, r.recon_unmatched_records, " +
            "COUNT(*) OVER() as total_count " +  // ← This gets the total count
            "FROM sys_brk_trans_reconciliation r " +
            "inner join sys_brk_trans_reconciliation_data d ON r.recon_data_id = d.recon_data_id " +
            "left join sys_brk_policies sbp on d.recon_data_pol_no = sbp.pol_no " +
            "left join sys_brk_accounts sba on sbp.pol_agent_id = sba.acct_id " +
            "WHERE (LOWER(d.recon_data_pol_client) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_no) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(:search) " +
            " OR LOWER(d.recon_data_pol_underwriter_no) LIKE LOWER(:search)) " +
            "AND (d.recon_data_date BETWEEN :dateFrom AND :dateTo) " +
            "AND (sba.acct_id = :accountCode OR :accountCode IS NULL) " +
            "AND (:unifiedSearch IS NULL OR :unifiedSearch = '' " +
            "    OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(CONCAT('%', :unifiedSearch, '%')) " +
            "    OR (LOWER(:unifiedSearch) IN ('reconciled', 'Y') AND r.recon_status = 'Reconciled') " +
            "    OR (LOWER(:unifiedSearch) IN ('not reconciled', 'Y', 'unreconciled') AND r.recon_status = 'Not Reconciled')) " +
            "GROUP BY r.recon_unmatched_records, d.recon_data_pol_no, d.recon_data_pol_client, " +
            "d.recon_data_pol_risk_note_no, d.recon_data_pol_underwriter_no, d.recon_underwriter_trans_code, " +
            "d.recon_data_pol_prem, d.recon_data_settlement, d.recon_data_balance, r.recon_status " +
            "ORDER BY d.recon_data_pol_no DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findTransReconciledData(@Param("search") String search,
                                           @Param("dateFrom") Date dateFrom,
                                           @Param("dateTo") Date dateTo,
                                           @Param("accountCode") Long accountCode,
                                           @Param("pageNo") int pageNo,
                                           @Param("limit") int limit,
                                           @Param("unifiedSearch") String unifiedSearch);


    @Query(value = "SELECT DISTINCT " +
            "sbp.pol_no, " +
            "sbc.client_fname, " +
            "sbc.client_onames, " +
            "acct.acct_name, " +
            "sbp.pol_client_pol_no, " +
            "sbp.pol_underwriter_trans_code, " +
            "sbmt.trans_ref_no, " +
            "sbp.pol_basic_premium_amt, " +
            "sbmt.trans_settle_amt, " +
            "sbmt.trans_balance, " +
            "sbmt.trans_date " +
            "FROM sys_brk_main_transactions sbmt " +
            "INNER JOIN sys_brk_policies sbp ON sbmt.trans_pol_id = sbp.pol_id " +
            "INNER JOIN sys_brk_accounts acct ON sbp.pol_agent_id = acct.acct_id " +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbmt.trans_clnt_code " +
            "WHERE sbmt.trans_type NOT IN ('RC', 'SAG', 'CN')" +
            "AND sbmt.trans_no = :transNo",
            nativeQuery = true)
    List<Object[]> findChildTrans(@Param("transNo") Long transNo);

    @Query(value = "SELECT trans_no FROM sys_brk_main_transactions sbmt WHERE sbmt.trans_type NOT IN ('RC', 'SAG', 'CN')", nativeQuery = true)
    List<BigInteger>getMainTrans();

    @Query(value = "SELECT DISTINCT r.recon_data_id " +
            "FROM sys_brk_trans_reconciliation r " +
            "INNER JOIN sys_brk_trans_reconciliation_data d ON r.recon_data_id = d.recon_data_id " +
            "LEFT JOIN sys_brk_policies sbp ON d.recon_data_pol_no = sbp.pol_no " +
            "LEFT JOIN sys_brk_accounts sba ON sbp.pol_agent_id = sba.acct_id " +
            "WHERE (:accountCode IS NULL OR sba.acct_id = :accountCode) " +
            "AND (d.recon_data_date BETWEEN :dateFrom AND :dateTo) " +
            "AND d.recon_data_status = 'Y' " +
            "AND (:unifiedSearch IS NULL OR :unifiedSearch = '' " +
            " OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(CONCAT('%', :unifiedSearch, '%')) " +
            " OR (LOWER(:unifiedSearch) IN ('reconciled', 'Y') AND r.recon_status = 'Reconciled') " +
            " OR (LOWER(:unifiedSearch) IN ('not reconciled', 'Y', 'unreconciled') AND r.recon_status = 'Not Reconciled'))",
            nativeQuery = true)
    List<Long> getReconDataIdsForDeletion(@Param("accountCode") Long accountCode,
                                          @Param("dateFrom") Date dateFrom,
                                          @Param("dateTo") Date dateTo,
                                          @Param("unifiedSearch") String unifiedSearch);

    @Modifying
    @Query(value = "DELETE FROM sys_brk_trans_reconciliation WHERE recon_data_id IN :reconDataIds", nativeQuery = true)
    int deleteReconciliationByIds(@Param("reconDataIds") List<Long> reconDataIds);

    @Modifying
    @Query(value = "DELETE FROM sys_brk_trans_reconciliation_data WHERE recon_data_id IN :reconDataIds", nativeQuery = true)
    int deleteReconciledDataByIds(@Param("reconDataIds") List<Long> reconDataIds);

}

package com.brokersystems.brokerapp.reconciliation.repository;

import com.brokersystems.brokerapp.reconciliation.ReconciliationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReconciliationDataRepo extends PagingAndSortingRepository<ReconciliationData, Long> {
    @Query(value = "SELECT d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, d.recon_data_pol_underwriter_no, d.recon_data_pol_prem, d.recon_data_pol_comm, d.recon_data_pol_paid_comm, d.recon_data_pol_total_comm, COUNT(d) " +
            "FROM sys_brk_reconciliation_data d " +
            "left join sys_brk_policies sbp on d.recon_data_pol_no = sbp.pol_no " +
            "left join sys_brk_accounts sba on sbp.pol_agent_id = sba.acct_id " +
            "WHERE (LOWER(d.recon_data_pol_client) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_no) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_risk_note_no) LIKE LOWER(:search)\n" +
            "    OR LOWER(d.recon_data_pol_underwriter_no) LIKE LOWER(:search)) " +
            "AND (d.recon_data_status = 'N') " +
            "AND (d.recon_data_date BETWEEN :dateFrom AND :dateTo) " +
            "AND (sbp.pol_agent_id = :accountCode OR :accountCode IS NULL) " +
            "GROUP BY d.recon_data_pol_no, d.recon_data_pol_client, d.recon_data_pol_risk_note_no, d.recon_data_pol_underwriter_no, d.recon_data_pol_prem, d.recon_data_pol_comm, d.recon_data_pol_paid_comm, d.recon_data_pol_total_comm " +
            "ORDER BY d.recon_data_pol_no DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findReconciliationData(@Param("search") String search,
                                          @Param("dateFrom") Date dateFrom,
                                          @Param("dateTo") Date dateTo,
                                          @Param("accountCode") Long accountCode,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit);

    List<ReconciliationData> findByPolicyNumber(String policyNumber);
}

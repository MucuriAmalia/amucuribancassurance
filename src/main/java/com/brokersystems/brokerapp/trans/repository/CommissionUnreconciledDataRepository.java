package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.CommissionReconciliationData;
import com.brokersystems.brokerapp.trans.model.CommissionUnreconciledData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface CommissionUnreconciledDataRepository extends PagingAndSortingRepository<CommissionUnreconciledData, Long> {


    @Query(value = "SELECT u.unrecon_data_id, u.unrecon_data_pol_no, u.unrecon_data_pol_client, " +
            "u.unrecon_data_dr_ref_no, u.unrecon_data_cr_ref_no, u.revision_no, " +
            "u.unrecon_data_payment, u.unrecon_data_pol_comm, " +
            "u.unrecon_data_pol_whtx, u.unrecon_data_pol_total_comm, " +
            "u.unrecon_data_reason, u.unrecon_data_system_client, " +
            "u.unrecon_data_system_comm, u.unrecon_data_system_whtx, " +
            "u.unrecon_data_system_payment, u.unrecon_data_created_date, " +
            "u.unrecon_data_batch_ref, " +
            "COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_commission_unreconciled_data u " +
            "WHERE unrecon_underwriter_code=:agentCode and (LOWER(u.unrecon_data_pol_client) LIKE LOWER(:search) " +
            "    OR LOWER(u.unrecon_data_pol_no) LIKE LOWER(:search) " +
            "    OR LOWER(u.unrecon_data_dr_ref_no) LIKE LOWER(:search) " +
            "    OR LOWER(u.unrecon_data_cr_ref_no) LIKE LOWER(:search)) " +
            "ORDER BY u.unrecon_data_created_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnreconciledCommissionData(
            @Param("search") String search,
            @Param("pageNo") int pageNo,
            @Param("limit") int limit,
            @Param("agentCode") Long agentCode);
    List<CommissionUnreconciledData> findByBatchReference(String batchReference);

    List<CommissionUnreconciledData> findByCreatedDateBetween(Date startDate, Date endDate);

    @Query("SELECT COUNT(u) FROM CommissionUnreconciledData u WHERE u.batchReference = :batchRef")
    Long countByBatchReference(@Param("batchRef") String batchReference);
}

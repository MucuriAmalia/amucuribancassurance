package com.brokersystems.brokerapp.trans.repository;

import com.brokersystems.brokerapp.trans.model.CommissionReconciliationData;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;


import java.util.Date;
import java.util.List;

public interface CommissionReconRepo extends PagingAndSortingRepository<CommissionReconciliationData, Long>{

    // Data query - no COUNT() OVER()
    @Query(value = "SELECT d.comm_recon_data_pol_no, d.comm_recon_data_pol_client, " +
            "d.comm_recon_data_dr_ref_no, d.comm_recon_data_cr_ref_no, d.revision_no, " +
            "d.comm_recon_data_payment, d.comm_recon_data_pol_comm, " +
            "d.comm_recon_data_pol_whtx, d.comm_recon_data_pol_total_comm,d.comm_recon_data_id,d.comm_recon_trans_code," +
            " d.comm_recon_data_uw_policy,d.comm_recon_data_pol_admin_fee,d.comm_recon_data_pol_admin_whtx,d.comm_recon_data_pol_total_revenue," +
            "COUNT(*) OVER() AS total_rows " +
            "FROM sys_brk_commission_reconciliation_data d " +
            "WHERE (LOWER(d.comm_recon_data_pol_client) LIKE LOWER(:search) " +
            "    OR LOWER(d.comm_recon_data_pol_no) LIKE LOWER(:search) " +
            "    OR LOWER(d.comm_recon_data_dr_ref_no) LIKE LOWER(:search) " +
            "    OR LOWER(d.comm_recon_data_cr_ref_no) LIKE LOWER(:search)) " +
            "AND (d.comm_recon_data_status = 'N') and d.comm_underwriter_code = :agentCode " +
            "ORDER BY d.comm_recon_data_pol_no DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findCommissionReconciliationData(
            @Param("search") String search,
            @Param("agentCode") Long agentCode,
            @Param("pageNo") int pageNo,
            @Param("limit") int limit);

    @Query(value = "SELECT COUNT(*) " +
                    "FROM sys_brk_commission_reconciliation_data d " +
                    "LEFT JOIN sys_brk_policies sbp ON d.comm_recon_data_pol_no = sbp.pol_no " +
                    "LEFT JOIN sys_brk_accounts sba ON sbp.pol_agent_id = sba.acct_id " +
                    "WHERE (LOWER(d.comm_recon_data_pol_client) LIKE LOWER(:search) " +
                    "    OR LOWER(d.comm_recon_data_pol_no) LIKE LOWER(:search) " +
                    "    OR LOWER(d.comm_recon_data_dr_ref_no) LIKE LOWER(:search) " +
                    "    OR LOWER(d.comm_recon_data_cr_ref_no) LIKE LOWER(:search)) " +
                    "AND (d.comm_recon_data_status = 'N')", nativeQuery = true)
            Long countCommissionReconciliationData(
                    @Param("search") String search);


    @Query(value = "select * from sys_brk_commission_reconciliation_data where comm_recon_data_dr_ref_no =:refNo and  comm_recon_data_cr_ref_no=:crref and  comm_recon_data_status =:status",nativeQuery = true)
    List<CommissionReconciliationData> checkDuplicateDebitTrans(@Param("refNo") String refNo,@Param("crref") String crref,@Param("status") String status);


//    // In SettlementRepo (you already have this)
//    List<Object[]> getPostgresUnsettledCommDetails(@Param("acctId")Long acctId, @Param("wef")Date wef, @Param("wet")Date wet, @Param("round")Long round);
}

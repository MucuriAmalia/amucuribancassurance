package com.brokersystems.brokerapp.updatepolno.repository;

import com.brokersystems.brokerapp.updatepolno.modal.UpdateClientPolNo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface UpdateClientPolNoRepo extends PagingAndSortingRepository<UpdateClientPolNo, Long>, QueryDslPredicateExecutor<UpdateClientPolNo> {

    @Query(value = "SELECT sbucp.update_id, sbc.client_fname , sbc.client_onames, " +
            "sbucp.insuremaster_pol_no, sbucp.underwriter_pol_no, sbucp.risk_note_no, " +
            "sbp.pr_desc ,sba.acct_name , sbucp.uploaded_date, sbucp.underwriter_trans_code," +
            " COUNT(*) OVER() as total_rows " +
            "FROM sys_brk_update_client_polno sbucp " +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbucp.pol_insurer " +
            "INNER JOIN sys_brk_clients sbc on sbc.client_id = sbucp.pol_client " +
            "INNER JOIN sys_brk_products sbp on sbp.pr_code = sbucp.updated_product " +
            "WHERE sbucp.update_status = 'N' " +
            "AND (LOWER(sbucp.client_id) like LOWER(:search) " +
            "OR LOWER(sbucp.client_pin) like LOWER(:search) " +
            "OR LOWER(sbucp.insuremaster_pol_no) like LOWER(:search) " +
            "OR LOWER(sbucp.underwriter_pol_no) like LOWER(:search) " +
            "OR LOWER(sbucp.risk_note_no) like LOWER(:search) " +
            "OR LOWER(sbucp.pol_product_name) like LOWER(:search) " +
            "OR LOWER(sbucp.insurer_code) like LOWER(:search) " +
            "OR CAST(sbucp.uploaded_date AS TEXT) like :search) " +
            "ORDER BY sbucp.uploaded_date DESC " +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnProcessedUpdates(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit);

    @Query(value = "SELECT sbp.pol_ref_no, sbc.client_fname, sbc.client_onames, sbp.pol_no, " +
            "sbp.pol_client_pol_no, sbp2.pr_desc, sbucp.processed_date, sbp.pol_auth_date, COUNT(*) OVER() as total_rows " +
            "FROM sys_brk_policies sbp " +
            "LEFT JOIN sys_brk_update_client_polno sbucp ON sbucp.policy_id = sbp.pol_id " +
            "INNER JOIN sys_brk_products sbp2 ON sbp2.pr_code = sbp.pol_prod_id " +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id " +
            "INNER JOIN sys_brk_accounts sba ON sba.acct_id = sbp.pol_agent_id " +
            "WHERE pol_auth_status = 'A' " +
            "AND pol_current_status != 'CN' " +
            "AND pol_rev_status != 'CN' " +
            "AND sbp.pol_auth_date BETWEEN :dateFrom AND :dateTo " +
            "AND sbp.pol_agent_id = :acctId " +
            "AND sbp.client_pol_update_status = :status " +
            "ORDER BY sbp.pol_auth_date DESC",
            nativeQuery = true)
    List<Object[]> getPolicyNoStatus(@Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo,
                                     @Param("acctId") Long acctId, @Param("status") Boolean status);



    @Query(value = "select * from sys_brk_update_client_polno where update_id = :polId", nativeQuery = true)
    UpdateClientPolNo findByUpdateId(@Param("polId") Long polId);

}

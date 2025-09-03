package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkStaffMotorsCreation;
import com.brokersystems.brokerapp.bulktransactions.models.GroupLife;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupLifeRepository extends PagingAndSortingRepository<GroupLife, Long>, QueryDslPredicateExecutor<GroupLife> {

    @Query(value = "SELECT sbgl.group_life_id , sbgl.group_cover_type, sbgl.group_product_name, sba.acct_name ,\n" +
            "sbgl.group_client_name, sbgl.group_client_other_names , sbgl.group_cover_from , sbgl.group_cover_to ,\n" +
            "sbgl.group_upload_date, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_group_life sbgl \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbgl.group_insurer_code \n" +
            "WHERE sbgl.group_trans_status = 'N'\n" +
            "AND sbgl.group_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbgl.group_client_name) like LOWER(:search) \n" +
            "AND LOWER(sbgl.group_client_other_names) like LOWER(:search) \n" +
            "OR LOWER(sbgl.group_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sbgl.group_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sbgl.group_cover_from AS TEXT) like :search \n" +
            "OR CAST(sbgl.group_cover_to AS TEXT) like :search \n" +
            "OR CAST(sbgl.group_upload_date AS TEXT) like :search) \n" +
            "ORDER BY sbgl.group_life_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedGroupLife(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "select distinct sbp.pol_id,sbc2.cov_desc,sbp.pol_no, sbp.pol_basic_premium_amt ,\n" +
            " sbc.client_fname, sbc.client_onames,sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date,\n" +
            "    CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
            "    WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
            "    WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
            "    WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
            "    WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
            "    WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
            "    WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data' \n" +
            "    WHEN sbp.pol_current_status = 'LP' THEN 'Loaded Policy' \n" +
            "            ELSE sbp.pol_current_status \n" +
            "       END AS pol_current_status, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_policies sbp \n" +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "INNER JOIN sys_brk_group_life sbgl ON sbgl.group_policy_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE sbp.pol_auth_status = 'LD' \n" +
//            "WHERE sbp.pol_auth_status = 'CV' \n" +
//            "AND sbgl.group_trans_status = 'Y'\n" +
            "WHERE  sbgl.bulk_policy_authorized = 'N' \n" +
            "AND sbgl.group_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedGroupLife(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_group_life where group_policy_pol_id = :id", nativeQuery = true)
    GroupLife findBulkStockByPolicyId(@Param("id") Long id);
}

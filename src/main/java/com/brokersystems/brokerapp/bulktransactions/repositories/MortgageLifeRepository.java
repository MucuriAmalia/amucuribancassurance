package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkStaffMotorsCreation;
import com.brokersystems.brokerapp.bulktransactions.models.MortgageLife;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MortgageLifeRepository extends PagingAndSortingRepository<MortgageLife, Long>, QueryDslPredicateExecutor<MortgageLife> {

    @Query(value = "SELECT sbmlr.mortgage_life_id , sbmlr.mortgage_cover_type, sbmlr.mortgage_product_name, sba.acct_name ,\n" +
            "sbmlr.mortgage_client_fname, sbmlr.mortgage_client_othernames , sbmlr.mortgage_cover_from , sbmlr.mortgage_cover_to ,sbmlr.mortgage_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_mortgage_life_renewals sbmlr \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbmlr.mortgage_insurer_code \n" +
            "WHERE sbmlr.mortgage_trans_status = 'N'\n" +
            "AND sbmlr.mortgage_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbmlr.mortgage_client_fname) like LOWER(:search) \n" +
            "AND LOWER(sbmlr.mortgage_client_othernames) like LOWER(:search) \n" +
            "OR LOWER(sbmlr.mortgage_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sbmlr.mortgage_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sbmlr.mortgage_cover_from AS TEXT) like :search \n" +
            "OR CAST(sbmlr.mortgage_cover_to AS TEXT) like :search \n" +
            "OR CAST(sbmlr.mortgage_upload_date AS TEXT) like :search) \n" +
            "ORDER BY sbmlr.mortgage_life_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedMortgageLife(@Param("search") String search,
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
            "    WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
            "    WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data' \n" +
            "            ELSE sbp.pol_current_status \n" +
            "       END AS pol_current_status, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_policies sbp \n" +
            "INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "INNER JOIN sys_brk_mortgage_life_renewals sbmlr ON sbmlr.mortgage_life_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            //"WHERE sbp.pol_auth_status = 'LD' \n" +
//            "WHERE sbp.pol_auth_status = 'R' \n" +
//            "AND sbmlr.mortgage_trans_status = 'Y'\n" +
            "WHERE  sbmlr.bulk_policy_authorized = 'N' \n" +
            "AND sbmlr.mortgage_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedMortgageLife(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_mortgage_life_renewals where mortgage_life_pol_id = :id", nativeQuery = true)
    MortgageLife findBulkStockByPolicyId(@Param("id") Long id);
}

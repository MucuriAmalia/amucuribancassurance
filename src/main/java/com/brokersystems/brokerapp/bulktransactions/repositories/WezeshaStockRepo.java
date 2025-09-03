package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkPolicyCreation;
import com.brokersystems.brokerapp.bulktransactions.models.WezeshaStockCreation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WezeshaStockRepo extends PagingAndSortingRepository<WezeshaStockCreation, Long>, QueryDslPredicateExecutor<WezeshaStockCreation> {
    @Query(value = "SELECT sbtws.wezesha_stock_id, sbtws.wezesha_loan_id, sbtws.wezesha_cover_type, sbtws.wezesha_client_fname, \n" +
            "sbtws.wezesha_client_othernames, sbtws.wezesha_business_type, sbtws.wezesha_sum_insured, sbtws.wezesha_stock_premium,\n" +
            "sbtws.wezesha_start_date,sbtws.wezesha_end_date, sbtws.wezesha_stock_uploaded_date, COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_trans_wezesha_stock sbtws \n" +
            "WHERE sbtws.wezesha_stock_trans_status = 'N' \n" +
            "AND sbtws.wezesha_stock_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbtws.wezesha_client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbtws.wezesha_client_othernames) like LOWER(:search) \n" +
            "OR LOWER(sbtws.wezesha_loan_id) like LOWER(:search) \n" +
            "OR LOWER(sbtws.wezesha_business_type) like LOWER(:search) \n" +
            "OR LOWER(sbtws.wezesha_cover_type) like LOWER(:search) \n" +
            "OR CAST(sbtws.wezesha_stock_uploaded_date AS TEXT) like :search \n" +
            "OR CAST(sbtws.wezesha_start_date AS TEXT) like :search \n" +
            "OR CAST(sbtws.wezesha_end_date AS TEXT) like :search) \n" +
            "ORDER BY sbtws.wezesha_stock_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnProcessedWezeshaPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit,
                                          @Param("currentUserId") Long currentUserId);

    @Query(value = "SELECT sbp.pol_id,sbc2.cov_desc,sbtws.wezesha_loan_id,sbp.pol_no, \n" +
            "       sbc.client_fname, sbc.client_onames, sbtws.wezesha_business_type, \n" +
            "       sbp.pol_wef_date, sbp.pol_wet_date, sbp.pol_date,\n" +
            "                   CASE WHEN sbp.pol_current_status = 'A' THEN 'Active' \n" +
            "                        WHEN sbp.pol_current_status = 'D' THEN 'Draft' \n" +
            "                        WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled' \n" +
            "                        WHEN sbp.pol_current_status = 'CO' THEN 'Converted' \n" +
            "                        WHEN sbp.pol_current_status = 'R' THEN 'Ready' \n" +
            "                        WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded' \n" +
            "                        WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy' \n" +
            "                        WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data' \n" +
            "                        ELSE sbp.pol_current_status \n" +
            "                   END AS pol_current_status , COUNT(*) OVER() as total_rows\n" +
            "            FROM sys_brk_policies sbp \n" +
            "            INNER JOIN sys_brk_clients sbc ON sbc.client_id = sbp.pol_client_id \n" +
            "            INNER JOIN sys_brk_trans_wezesha_stock sbtws ON sbtws.wezesha_stock_pol_id = sbp.pol_id \n" +
            "            INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "            INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
            "            WHERE pol_auth_status != 'A' \n" +
            "            AND sbtws.wezesha_stock_processed_by <> :currentUserId \n" +
            "            AND sbtws.wezesha_stock_trans_status = 'Y' \n" +
            //"            WHERE sbtws.wezesha_stock_authorized = 'N' \n" +
            "            AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "            OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "            OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "            OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "            OR CAST(sbp.pol_wef_date AS TEXT) like :search \n" +
            "            OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "            OR CAST(sbp.pol_date AS TEXT) like :search) \n" +
            "            ORDER BY sbp.pol_id desc \n" +
            "            OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedwezeshaBulkPol(@Param("search") String search,
                                          @Param("pageNo") int pageNo,
                                          @Param("limit") int limit,
                                          @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_trans_wezesha_stock where wezesha_stock_pol_id = :id", nativeQuery = true)
    WezeshaStockCreation findWezeshaStockByPolicyId(@Param("id") Long id);

    @Query(value = "SELECT * FROM sys_brk_trans_wezesha_stock where wezesha_stock_pol_id = :wezeshaId", nativeQuery = true)
    WezeshaStockCreation findWithPolicyTransByWezeshaId(@Param("wezeshaId") Long wezeshaId);

}

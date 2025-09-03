package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkStaffMotorsCreation;
import com.brokersystems.brokerapp.bulktransactions.models.CreditCard;
import com.brokersystems.brokerapp.bulktransactions.models.CreditShield;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditShieldRepository extends PagingAndSortingRepository<CreditShield, Long>, QueryDslPredicateExecutor<CreditShield> {

    @Query(value = "SELECT sbcc.shield_id , sbcc.card_cover_type, sbcc.card_product_name, sba.acct_name ,\n" +
            "sbcc.card_client_fname, sbcc.card_client_othernames , sbcc.card_cover_from , sbcc.card_cover_to ,sbcc.card_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_credit_shield_bulk_upload sbcc \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sbcc.card_insurer_code \n" +
            "WHERE sbcc.card_trans_status = 'N'\n" +
            "AND sbcc.card_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sbcc.card_client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbcc.card_client_othernames) like LOWER(:search) \n" +
            "OR LOWER(sbcc.card_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sbcc.card_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sbcc.card_cover_from AS TEXT) like :search \n" +
            "OR CAST(sbcc.card_cover_to AS TEXT) like :search \n" +
            "OR CAST(sbcc.card_upload_date AS TEXT) like :search) \n" +
            "ORDER BY sbcc.shield_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedCreditCard(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "SELECT DISTINCT \n" +
            "    sbp.pol_id,\n" +
            "    sbc2.cov_desc,\n" +
            "    sbp.pol_no,\n" +
            "    sbp.pol_basic_premium_amt,\n" +
            "    sbc.client_fname, \n" +
            "    sbc.client_onames,\n" +
            "    sbp.pol_wef_date,\n" +
            "    sbp.pol_wet_date,\n" +
            "    sbp.pol_date,\n" +
            "    CASE \n" +
            "        WHEN sbp.pol_current_status = 'A'  THEN 'Active'\n" +
            "        WHEN sbp.pol_current_status = 'D'  THEN 'Draft'\n" +
            "        WHEN sbp.pol_current_status = 'CN' THEN 'Cancelled'\n" +
            "        WHEN sbp.pol_current_status = 'CO' THEN 'Converted'\n" +
            "        WHEN sbp.pol_current_status = 'R'  THEN 'Ready'\n" +
            "        WHEN sbp.pol_current_status = 'PL' THEN 'Pre-Loaded'\n" +
            "        WHEN sbp.pol_current_status = 'PD' THEN 'Loaded Policy'\n" +
            "        WHEN sbp.pol_current_status = 'LD' THEN 'Loaded Data'\n" +
            "        ELSE sbp.pol_current_status\n" +
            "    END AS pol_current_status,\n" +
            "    COUNT(*) OVER() AS total_rows\n" +
            "FROM sys_brk_policies sbp\n" +
            "INNER JOIN sys_brk_clients sbc \n" +
            "    ON sbc.client_id = sbp.pol_client_id\n" +
            "INNER JOIN sys_brk_credit_shield_bulk_upload sbcc \n" +
            "    ON sbcc.card_pol_id = sbp.pol_id\n" +
            "INNER JOIN sys_brk_risks sbr \n" +
            "    ON sbr.risk_pol_id = sbp.pol_id\n" +
            "INNER JOIN sys_brk_covertypes sbc2  \n" +
            "    ON sbc2.cov_id = sbr.risk_cover_id\n" +
            //"WHERE sbp.pol_auth_status = 'LD'\n" +
//            "WHERE sbp.pol_auth_status = 'CV'\n" +
//            "  AND sbcc.card_trans_status = 'Y'\n" +
            "WHERE  sbcc.bulk_policy_authorized = 'N' \n" +
            "AND sbcc.card_processed_by <> :currentUserId \n" +
            "  AND (\n" +
            "        LOWER(sbc2.cov_desc) LIKE LOWER(:search)\n" +
            "        OR LOWER(sbp.pol_no) LIKE LOWER(:search)\n" +
            "        OR LOWER(sbc.client_fname) LIKE LOWER(:search)\n" +
            "        OR LOWER(sbc.client_onames) LIKE LOWER(:search)\n" +
            "        OR CAST(sbp.pol_wef_date AS TEXT) LIKE :search\n" +
            "        OR CAST(sbp.pol_wet_date AS TEXT) LIKE :search\n" +
            "        OR CAST(sbp.pol_date AS TEXT) LIKE :search\n" +
            "      )\n" +
            "ORDER BY sbp.pol_id DESC \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedCreditCard(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_credit_shield_bulk_upload where card_pol_id = :id", nativeQuery = true)
    CreditShield findBulkStockByPolicyId(@Param("id") Long id);

    @Query("SELECT c.sheildId FROM CreditShield c")
    List<Long> findAllIds();
}

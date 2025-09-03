package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkStaffMotorsCreation;
import com.brokersystems.brokerapp.bulktransactions.models.CreditCard;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CreditCardRepository extends PagingAndSortingRepository<CreditCard, Long>, QueryDslPredicateExecutor<CreditCard> {

    @Query(value = "SELECT sbcc.card_id , sbcc.card_cover_type, sbcc.card_product_name, sba.acct_name ,\n" +
            "sbcc.card_client_fname, sbcc.card_client_othernames , sbcc.card_cover_from , sbcc.card_cover_to ,sbcc.card_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_credit_card sbcc \n" +
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
            "ORDER BY sbcc.card_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedCreditCard(@Param("search") String search,
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
            "INNER JOIN sys_brk_credit_card sbcc ON sbcc.card_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
//            "WHERE sbp.pol_auth_status = 'LD' \n" +
//            "AND sbcc.card_trans_status = 'Y'\n" +
            "WHERE  sbcc.bulk_policy_authorized = 'N' \n" +
            "AND sbcc.card_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedCreditCard(@Param("search") String search,
                                             @Param("pageNo") int pageNo,
                                             @Param("limit") int limit,
                                             @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_credit_card where card_pol_id = :id", nativeQuery = true)
    CreditCard findBulkStockByPolicyId(@Param("id") Long id);
}

package com.brokersystems.brokerapp.bulktransactions.repositories;

import com.brokersystems.brokerapp.bulktransactions.models.BulkStaffMotorsCreation;
import com.brokersystems.brokerapp.bulktransactions.models.EmbedRetrenchInsurance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmbedRetrenchInsuranceRepo extends PagingAndSortingRepository<EmbedRetrenchInsurance, Long>, QueryDslPredicateExecutor<EmbedRetrenchInsurance> {

    @Query(value = "SELECT sberi.retrench_id , sberi.retrench_cover_type, sberi.embed_product_name, sba.acct_name ,\n" +
            "sberi.retrench_client_name, sberi.retrench_client_other_names , sberi.retrench_cover_from , sberi.retrench_cover_to ,sberi.retrench_upload_date," +
            "COUNT(*) OVER() as total_rows \n" +
            "FROM sys_brk_embed_retrench_insurance sberi \n" +
            "INNER JOIN sys_brk_accounts sba on sba.acct_id = sberi.retrench_insurer_code \n" +
            "WHERE sberi.retrench_trans_status = 'N'\n" +
            "AND sberi.retrench_uploaded_by = :currentUserId \n" +
            "AND (LOWER(sberi.retrench_client_name) like LOWER(:search) \n" +
            "OR LOWER(sberi.retrench_cover_type) like LOWER(:search) \n" +
            "OR LOWER(sberi.embed_product_name) like LOWER(:search) \n" +
            "OR LOWER(sba.acct_name) like LOWER(:search) \n" +
            "OR CAST(sberi.retrench_cover_from AS TEXT) like :search \n" +
            "OR CAST(sberi.retrench_cover_to AS TEXT) like :search \n" +
            "OR CAST(sberi.retrench_upload_date AS TEXT) like :search) \n" +
            "ORDER BY sberi.retrench_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> findUnprocessedEmbedRetrench(@Param("search") String search,
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
            "INNER JOIN sys_brk_embed_retrench_insurance sberi ON sberi.retrench_policy_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_risks sbr ON sbr.risk_pol_id = sbp.pol_id \n" +
            "INNER JOIN sys_brk_covertypes sbc2 ON sbc2.cov_id = sbr.risk_cover_id \n" +
//            "WHERE sbp.pol_auth_status = 'LD' \n" +
//            "AND sberi.retrench_trans_status = 'Y'\n" +
            "WHERE  sberi.bulk_policy_authorized = 'N' \n" +
            "AND sberi.retrench_processed_by <> :currentUserId \n" +
            "AND (LOWER(sbc2.cov_desc) like LOWER(:search) \n" +
            "OR LOWER(sbp.pol_no) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_fname) like LOWER(:search) \n" +
            "OR LOWER(sbc.client_onames) like LOWER(:search) \n" +
            "OR CAST(sbp.pol_wef_date AS TEXT) like :search\n" +
            "OR CAST(sbp.pol_wet_date AS TEXT) like :search \n" +
            "OR CAST(sbp.pol_date AS TEXT) like :search)\n" +
            "ORDER BY sbp.pol_id desc \n" +
            "OFFSET :pageNo * :limit LIMIT :limit", nativeQuery = true)
    List<Object[]> viewUnprocessedEmbedRetrench(@Param("search") String search,
                                                    @Param("pageNo") int pageNo,
                                                    @Param("limit") int limit,
                                                    @Param("currentUserId") Long currentUserId);

    @Query(value = "select  * from sys_brk_embed_retrench_insurance where retrench_policy_pol_id = :id", nativeQuery = true)
    EmbedRetrenchInsurance findBulkStockByPolicyId(@Param("id") Long id);
}
